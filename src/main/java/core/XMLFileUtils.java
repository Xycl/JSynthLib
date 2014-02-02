package core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLFileUtils {

    private static final Logger LOG = Logger.getLogger(XMLFileUtils.class);

    private static final String patchLibXMLName = "patchlib";
    private static final String patchXMLName = "patch";
    private static final String authorXMLName = "author";
    private static final String dateXMLName = "date";
    private static final String commentXMLName = "comment";
    private static final String patchDataXMLName = "patchData";

    private static final boolean compressPatchData = true;

    public static void writePatchBasket(PatchBasket pb, String filename) {

        Document document = null;

        DocumentBuilder builder = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
            document = builder.newDocument();
        } catch (ParserConfigurationException e) {
            LOG.warn(e.getMessage(), e);
        }
        Element root = (Element) document.createElement(patchLibXMLName);
        document.appendChild(root);

        ArrayList pblist = pb.getPatchCollection();
        for (int i = 0; i < pblist.size(); i++) {
            Patch patch = (Patch) pblist.get(i);
            Element patchElement = document.createElement(patchXMLName);
            patchElement.setAttribute(authorXMLName, patch.getAuthor());
            patchElement.setAttribute(dateXMLName, patch.getDate());
            patchElement.setAttribute(commentXMLName, patch.getComment());
            Element patchDataElement =
                    (Element) document.createElement(patchDataXMLName);

            if (compressPatchData) {
                patchDataElement.setAttribute("encoding", "GZIP:Base64");
                Text contents =
                        document.createTextNode(Base64.encodeBytes(patch.sysex,
                                Base64.GZIP));
                patchDataElement.appendChild(contents);
            } else {
                patchDataElement.setAttribute("encoding", "Base64");
                Text contents =
                        document.createTextNode(Base64.encodeBytes(patch.sysex,
                                Base64.GZIP));
                patchDataElement.appendChild(contents);
            }
            patchElement.appendChild(patchDataElement);
            root.appendChild(patchElement);
        }
        writeXmlToFile(filename, document);
    }

    public static void readPatchBasket(PatchBasket pb, String filename) {
        Document doc = parse(filename);

        Element root = (Element) doc.getChildNodes().item(0);
        if (root.getNodeName().equals(patchLibXMLName)) {
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element patchElement = (Element) children.item(i);
                    if (patchElement.getNodeName().equals(patchXMLName)) {
                        LOG.info(authorXMLName + " : "
                                + patchElement.getAttribute(authorXMLName));
                        LOG.info(dateXMLName + " : "
                                + patchElement.getAttribute(dateXMLName));
                        LOG.info(commentXMLName + " : "
                                + patchElement.getAttribute(commentXMLName));

                        // Patch data is held in a Text node within the
                        // patchdata node
                        Element patchDataElement =
                                (Element) patchElement.getElementsByTagName(
                                        patchDataXMLName).item(0);
                        byte patchdata[] =
                                Base64.decode(patchDataElement.getChildNodes()
                                        .item(0).getNodeValue());
                        LOG.info("Patch data length = " + patchdata.length);
                        Patch patch = new Patch(patchdata);
                        patch.setAuthor(patchElement
                                .getAttribute(authorXMLName));
                        patch.setDate(patchElement.getAttribute(dateXMLName));
                        patch.setComment(patchElement
                                .getAttribute(commentXMLName));
                        pb.pastePatch(patch);
                    } else {
                        LOG.info("Invalid patch element");
                    }
                }
            }
        } else {
            LOG.info("Invalid patchlib element");
        }
    }

    public static Document parse(String fileName) {
        Document document = null;
        // Initiate DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // To get a validating parser
        factory.setValidating(false);
        // To get one that understands namespaces
        factory.setNamespaceAware(true);

        try {
            // Get DocumentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Parse and load into memory the Document
            document = builder.parse(new File(fileName));
            return document;

        } catch (SAXParseException spe) {
            // Error generated by the parser
            LOG.info("\n** Parsing error , line " + spe.getLineNumber()
                    + ", uri " + spe.getSystemId());
            LOG.info(" " + spe.getMessage());
            // Use the contained exception, if any
            Exception x = spe;
            if (spe.getException() != null)
                x = spe.getException();
            LOG.warn(x.getMessage(), x);
        } catch (SAXException sxe) {
            // Error generated during parsing
            Exception x = sxe;
            if (sxe.getException() != null)
                x = sxe.getException();
            LOG.warn(x.getMessage(), x);
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            LOG.warn(pce.getMessage(), pce);
        } catch (IOException ioe) {
            // I/O error
            LOG.warn(ioe.getMessage(), ioe);
        }

        return null;
    }

    public static void writeXmlToFile(String filename, Document document) {
        try {
            // Prepare the DOM document for writing
            Source source = new DOMSource(document);

            // Prepare the output file
            File file = new File(filename);
            Result result = new StreamResult(file);

            // Write the DOM document to the file
            // Get Transformer
            Transformer xformer =
                    TransformerFactory.newInstance().newTransformer();
            // These two lines set up the indenting of the XML. If you don't
            // have these,
            // the XML file will be all one line.
            xformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "2");

            // Write to a file
            xformer.transform(source, result);

        } catch (TransformerConfigurationException e) {
            LOG.info(e.getMessage(), e);
        } catch (TransformerException e) {
            LOG.info(e.getMessage(), e);
        }
    }
}
