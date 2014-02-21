package synthdrivers.RocktronIntellifex;

import core.*;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsynthlib.view.PatchEditorFrame;
import org.jsynthlib.view.widgets.ComboBoxWidget;
import org.jsynthlib.view.widgets.PatchNameWidget;

/**
 * Preset editor for Rocktron Intellifex.
 * @author Klaus Sailer
 * @version $Id$
 */
class RocktronIntellifexPresetEditor extends PatchEditorFrame {

    private final String[] configName = new String[] {
            "Hush,Chorus,Delay,Reverb", "Hush,PShift,Delay,Reverb",
            "Hush,Reverb", "Hush,Delay,Ducker", "Hush,8V-Chorus,Delay",
            "Hush,PShift,Delay" };

    private final String[] lvl6dbOptions = new String[] {
            "    -oo", "  -42dB", "  -36dB", "  -32dB", "  -30dB", "  -28dB",
            "  -26dB", "  -25dB", "  -24dB", "  -23dB", "  -22dB", "  -21dB",
            "  -20dB", "  -19dB", "  -18dB", "  -17dB", "  -16dB", "  -15dB",
            "  -14dB", "-13.5dB", "-13.0dB", "-12.5dB", "-12.0dB", "-11.5dB",
            "-11.0dB", "-10.5dB", "-10.0dB", " -9.5dB", " -9.0dB", " -8.5dB",
            " -8.0dB", " -7.5dB", " -7.0dB", " -6.5dB", " -6.0dB", " -5.5dB",
            " -5.0dB", " -4.5dB", " -4.0dB", " -3.5dB", " -3.0dB", " -2.5dB",
            " -2.0dB", " -1.5dB", " -1.0dB", "  -.5dB", "  0.0dB", "  +.5dB",
            " +1.0dB", " +1.5dB", " +2.0dB", " +2.5dB", " +3.0dB", " +3.5dB",
            " +4.0dB", " +4.5dB", " +5.0dB", " +5.5dB", " +6.0dB" };

    private final String[] lvl0dbOptions = new String[] {
            "    -oo", "  -48dB", "  -42dB", "  -38dB", "  -36dB", "  -34dB",
            "  -32dB", "  -31dB", "  -30dB", "  -29dB", "  -28dB", "  -27dB",
            "  -26dB", "  -25dB", "  -24dB", "  -23dB", "  -22dB", "  -21dB",
            "  -20dB", "-19.5dB", "-19.0dB", "-18.5dB", "-18.0dB", "-17.5dB",
            "-17.0dB", "-16.5dB", "-16.0dB", "-15.5dB", "-15.0dB", "-14.5dB",
            "-14.0dB", "-13.5dB", "-13.0dB", "-12.5dB", "-12.0dB", "-11.5dB",
            "-11.0dB", "-10.5dB", "-10.0dB", " -9.5dB", " -9.0dB", " -8.5dB",
            " -8.0dB", " -7.5dB", " -7.0dB", " -6.5dB", " -6.0dB", " -5.5dB",
            " -5.0dB", " -4.5dB", " -4.0dB", " -3.5dB", " -3.0dB", " -2.5dB",
            " -2.0dB", " -1.5dB", " -1.0dB", "  -.5dB", "  0.0dB" };

    private final String[] sens_92_20Options = new String[] {
            "-92dB", "-89dB", "-86dB", "-83dB", "-80dB", "-77dB", "-74dB",
            "-71dB", "-68dB", "-65dB", "-62dB", "-59dB", "-56dB", "-53dB",
            "-50dB", "-47dB", "-44dB", "-41dB", "-38dB", "-35dB", "-32dB",
            "-29dB", "-26dB", "-23dB", "-20dB" };

    private final String[] relTimeOptions = new String[] {
            " 25ms", " 50ms", " 75ms", "100ms", "150ms", "200ms", "300ms",
            "400ms", "600ms", "800ms" };

    private final String[] panOptions = new String[] {
            "L<50", "L<49", "L<48", "L<47", "L<46", "L<45", "L<44", "L<43",
            "L<42", "L<41", "L<40", "L<39", "L<38", "L<37", "L<36", "L<35",
            "L<34", "L<33", "L<32", "L<31", "L<30", "L<29", "L<28", "L<27",
            "L<26", "L<25", "L<24", "L<23", "L<22", "L<21", "L<20", "L<19",
            "L<18", "L<17", "L<16", "L<15", "L<14", "L<13", "L<12", "L<11",
            "L<10", "L<9", "L<8", "L<7", "L<6", "L<5", "L<4", "L<3", "L<2",
            "L<1", "L<>R", "1>R", "2>R", "3>R", "4>R", "5>R", "6>R", "7>R",
            "8>R", "9>R", "10>R", "11>R", "12>R", "13>R", "14>R", "15>R",
            "16>R", "17>R", "18>R", "19>R", "20>R", "21>R", "22>R", "23>R",
            "24>R", "25>R", "26>R", "27>R", "28>R", "29>R", "30>R", "31>R",
            "32>R", "33>R", "34>R", "35>R", "36>R", "37>R", "38>R", "39>R",
            "40>R", "41>R", "42>R", "43>R", "44>R", "45>R", "46>R", "47>R",
            "48>R", "49>R", "50>R" };

    private final String[] dirdlymixOptions = new String[] {
            "Dir<50", "Dir<49", "Dir<48", "Dir<47", "Dir<46", "Dir<45",
            "Dir<44", "Dir<43", "Dir<42", "Dir<41", "Dir<40", "Dir<39",
            "Dir<38", "Dir<37", "Dir<36", "Dir<35", "Dir<34", "Dir<33",
            "Dir<32", "Dir<31", "Dir<30", "Dir<29", "Dir<28", "Dir<27",
            "Dir<26", "Dir<25", "Dir<24", "Dir<23", "Dir<22", "Dir<21",
            "Dir<20", "Dir<19", "Dir<18", "Dir<17", "Dir<16", "Dir<15",
            "Dir<14", "Dir<13", "Dir<12", "Dir<11", "Dir<10", "Dir<9", "Dir<8",
            "Dir<7", "Dir<6", "Dir<5", "Dir<4", "Dir<3", "Dir<2", "Dir<1",
            "Dir<>Dly", "1>Dly", "2>Dly", "3>Dly", "4>Dly", "5>Dly", "6>Dly",
            "7>Dly", "8>Dly", "9>Dly", "10>Dly", "11>Dly", "12>Dly", "13>Dly",
            "14>Dly", "15>Dly", "16>Dly", "17>Dly", "18>Dly", "19>Dly",
            "20>Dly", "21>Dly", "22>Dly", "23>Dly", "24>Dly", "25>Dly",
            "26>Dly", "27>Dly", "28>Dly", "29>Dly", "30>Dly", "31>Dly",
            "32>Dly", "33>Dly", "34>Dly", "35>Dly", "36>Dly", "37>Dly",
            "38>Dly", "39>Dly", "40>Dly", "41>Dly", "42>Dly", "43>Dly",
            "44>Dly", "45>Dly", "46>Dly", "47>Dly", "48>Dly", "49>Dly",
            "50>Dly" };

    private final String[] delayOptions = new String[] {
            "  0ms", "  2ms", "  4ms", "  6ms", "  8ms", " 10ms", " 12ms",
            " 14ms", " 16ms", " 18ms", " 20ms", " 22ms", " 24ms", " 26ms",
            " 28ms", " 30ms", " 32ms", " 34ms", " 36ms", " 38ms", " 40ms",
            " 42ms", " 44ms", " 46ms", " 48ms", " 50ms", " 52ms", " 54ms",
            " 56ms", " 58ms", " 60ms", " 62ms", " 64ms", " 66ms", " 68ms",
            " 70ms", " 72ms", " 74ms", " 76ms", " 78ms", " 80ms", " 82ms",
            " 84ms", " 86ms", " 88ms", " 90ms", " 92ms", " 94ms", " 96ms",
            " 98ms", "100ms", "102ms", "104ms", "106ms", "108ms", "110ms",
            "112ms", "114ms", "116ms", "118ms", "120ms", "122ms", "124ms",
            "126ms", "128ms", "130ms", "132ms", "134ms", "136ms", "138ms",
            "140ms", "142ms", "144ms", "146ms", "148ms", "150ms", "152ms",
            "154ms", "156ms", "158ms", "160ms", "162ms", "164ms", "166ms",
            "168ms", "170ms", "172ms", "174ms", "176ms", "178ms", "180ms",
            "182ms", "184ms", "186ms", "188ms", "190ms", "192ms", "194ms",
            "196ms", "198ms", "200ms", "202ms", "204ms", "206ms", "208ms",
            "210ms", "212ms", "214ms", "216ms", "218ms", "220ms", "222ms",
            "224ms", "226ms", "228ms", "230ms", "232ms", "234ms", "236ms",
            "238ms", "240ms", "242ms", "244ms", "246ms", "248ms", "250ms",
            "252ms", "254ms", "256ms", "258ms", "260ms", "262ms", "264ms",
            "266ms", "268ms", "270ms", "272ms", "274ms", "276ms", "278ms",
            "280ms", "282ms", "284ms", "286ms", "288ms", "290ms", "292ms",
            "294ms", "296ms", "298ms", "300ms", "302ms", "304ms", "306ms",
            "308ms", "310ms", "312ms", "314ms", "316ms", "318ms", "320ms",
            "322ms", "324ms", "326ms", "328ms", "330ms", "332ms", "334ms",
            "336ms", "338ms", "340ms", "342ms", "344ms", "346ms", "348ms",
            "350ms", "352ms", "354ms", "356ms", "358ms", "360ms", "362ms",
            "364ms", "366ms", "368ms", "370ms", "372ms", "374ms", "376ms",
            "378ms", "380ms", "382ms", "384ms", "386ms", "388ms", "390ms",
            "392ms", "394ms", "396ms", "398ms", "400ms", "402ms", "404ms",
            "406ms", "408ms", "410ms", "412ms", "414ms", "416ms", "418ms" };

    private final String[] predelayOptions = new String[] {
            "  0ms", "  1ms", "  2ms", "  3ms", "  4ms", "  5ms", "  6ms",
            "  7ms", "  8ms", "  9ms", " 10ms", " 11ms", " 12ms", " 13ms",
            " 14ms", " 15ms", " 16ms", " 17ms", " 18ms", " 19ms", " 20ms",
            " 21ms", " 22ms", " 23ms", " 24ms", " 25ms", " 26ms", " 27ms",
            " 28ms", " 29ms", " 30ms", " 31ms", " 32ms", " 33ms", " 34ms",
            " 35ms", " 36ms", " 37ms", " 38ms", " 39ms", " 40ms", " 41ms",
            " 42ms", " 43ms", " 44ms", " 45ms", " 46ms", " 47ms", " 48ms",
            " 49ms", " 50ms", " 51ms", " 52ms", " 53ms", " 54ms", " 55ms",
            " 56ms", " 57ms", " 58ms", " 59ms", " 60ms", " 61ms", " 62ms",
            " 63ms", " 64ms", " 65ms", " 66ms", " 67ms", " 68ms", " 69ms",
            " 70ms", " 71ms", " 72ms", " 73ms", " 74ms", " 75ms", " 76ms",
            " 77ms", " 78ms", " 79ms", " 80ms", " 81ms", " 82ms", " 83ms",
            " 84ms", " 85ms", " 86ms", " 87ms", " 88ms", " 89ms", " 90ms",
            " 91ms", " 92ms", " 93ms", " 94ms", " 95ms", " 96ms", " 97ms",
            " 98ms", " 99ms", "100ms", "101ms", "102ms", "103ms", "104ms",
            "105ms", "106ms", "107ms", "108ms", "109ms", "110ms", "111ms",
            "112ms", "113ms", "114ms", "115ms", "116ms", "117ms", "118ms",
            "119ms", "120ms", "121ms", "122ms", "123ms", "124ms", "125ms",
            "126ms", "127ms", "128ms", "129ms", "130ms", "131ms", "132ms",
            "133ms", "134ms", "135ms", "136ms", "137ms", "138ms", "139ms",
            "140ms", "141ms", "142ms", "143ms", "144ms", "145ms", "146ms",
            "147ms", "148ms", "149ms", "150ms", "151ms", "152ms", "153ms",
            "154ms", "155ms", "156ms", "157ms", "158ms", "159ms", "160ms",
            "161ms", "162ms", "163ms", "164ms", "165ms", "166ms", "167ms",
            "168ms", "169ms", "170ms", "171ms", "172ms", "173ms", "174ms",
            "175ms", "176ms", "177ms", "178ms", "179ms", "180ms", "181ms",
            "182ms", "183ms", "184ms", "185ms", "186ms", "187ms", "188ms",
            "189ms", "190ms", "191ms", "192ms", "193ms", "194ms", "195ms",
            "196ms", "197ms", "198ms", "199ms", "200ms", "201ms", "202ms",
            "203ms", "204ms", "205ms", "206ms", "207ms", "208ms", "209ms" };

    private final String[] fineDelayOptions = new String[] {
            "  0ms", "  5ms", " 10ms", " 15ms", " 20ms", " 25ms", " 30ms",
            " 35ms", " 40ms", " 45ms", " 50ms", " 55ms", " 60ms", " 65ms",
            " 70ms", " 75ms", " 80ms", " 85ms", " 90ms", " 95ms", "100ms",
            "105ms", "110ms", "115ms", "120ms", "125ms", "130ms", "135ms",
            "140ms", "145ms", "150ms", "155ms", "160ms", "165ms", "170ms",
            "175ms", "180ms", "185ms", "190ms", "195ms", "200ms", "205ms",
            "210ms", "215ms", "220ms", "225ms", "230ms", "235ms", "240ms",
            "245ms", "250ms", "255ms", "260ms", "265ms", "270ms", "275ms",
            "280ms", "285ms", "290ms", "295ms", "300ms", "305ms", "310ms",
            "315ms", "320ms", "325ms", "330ms", "335ms", "340ms", "345ms",
            "350ms", "355ms", "360ms", "365ms", "370ms", "375ms", "380ms",
            "385ms", "390ms", "395ms", "400ms", "405ms", "410ms", "415ms",
            "420ms", "425ms", "430ms", "435ms", "440ms", "445ms", "450ms",
            "455ms", "460ms", "465ms", "470ms", "475ms", "480ms", "485ms",
            "490ms", "495ms", "500ms", "505ms", "510ms", "515ms", "520ms",
            "525ms", "530ms", "535ms", "540ms", "545ms", "550ms", "555ms",
            "560ms", "565ms", "570ms", "575ms", "580ms", "585ms", "590ms",
            "595ms", "600ms", "605ms", "610ms", "615ms", "620ms", "625ms",
            "630ms", "635ms", "640ms", "645ms", "650ms", "655ms", "660ms",
            "665ms", "670ms", "675ms", "680ms", "685ms", "690ms", "695ms",
            "700ms", "705ms", "710ms", "715ms", "720ms", "725ms", "730ms",
            "735ms", "740ms", "745ms", "750ms" };

    private final String[] coarseDelayOptions = new String[] {
            "   0ms", "  10ms", "  20ms", "  30ms", "  40ms", "  50ms",
            "  60ms", "  70ms", "  80ms", "  90ms", " 100ms", " 110ms",
            " 120ms", " 130ms", " 140ms", " 150ms", " 160ms", " 170ms",
            " 180ms", " 190ms", " 200ms", " 210ms", " 220ms", " 230ms",
            " 240ms", " 250ms", " 260ms", " 270ms", " 280ms", " 290ms",
            " 300ms", " 310ms", " 320ms", " 330ms", " 340ms", " 350ms",
            " 360ms", " 370ms", " 380ms", " 390ms", " 400ms", " 410ms",
            " 420ms", " 430ms", " 440ms", " 450ms", " 460ms", " 470ms",
            " 480ms", " 490ms", " 500ms", " 510ms", " 520ms", " 530ms",
            " 540ms", " 550ms", " 560ms", " 570ms", " 580ms", " 590ms",
            " 600ms", " 610ms", " 620ms", " 630ms", " 640ms", " 650ms",
            " 660ms", " 670ms", " 680ms", " 690ms", " 700ms", " 710ms",
            " 720ms", " 730ms", " 740ms", " 750ms", " 760ms", " 770ms",
            " 780ms", " 790ms", " 800ms", " 810ms", " 820ms", " 830ms",
            " 840ms", " 850ms", " 860ms", " 870ms", " 880ms", " 890ms",
            " 900ms", " 910ms", " 920ms", " 930ms", " 940ms", " 950ms",
            " 960ms", " 970ms", " 980ms", " 990ms", "1000ms", "1010ms",
            "1020ms", "1030ms", "1040ms", "1050ms", "1060ms", "1070ms",
            "1080ms", "1090ms", "1100ms", "1110ms", "1120ms", "1130ms",
            "1140ms", "1150ms", "1160ms", "1170ms", "1180ms", "1190ms",
            "1200ms", "1210ms", "1220ms", "1230ms", "1240ms", "1250ms",
            "1260ms", "1270ms", "1280ms", "1290ms", "1300ms", "1310ms",
            "1320ms", "1330ms", "1340ms", "1350ms", "1360ms", "1370ms",
            "1380ms", "1390ms", "1400ms", "1410ms", "1420ms", "1430ms",
            "1440ms", "1450ms", "1460ms", "1470ms", "1480ms", "1490ms",
            "1500ms" };

    private final String[] pitchOptions = new String[] {

            "-2400", "-2380", "-2360", "-2340", "-2320", "-2300", "-2280",
            "-2260", "-2240", "-2220", "-2200", "-2180", "-2160", "-2140",
            "-2120", "-2100", "-2080", "-2060", "-2040", "-2020", "-2000",
            "-1980", "-1960", "-1940", "-1920", "-1900", "-1880", "-1860",
            "-1840", "-1820", "-1800", "-1780", "-1760", "-1740", "-1720",
            "-1700", "-1680", "-1660", "-1640", "-1620", "-1600", "-1580",
            "-1560", "-1540", "-1520", "-1500", "-1480", "-1460", "-1440",
            "-1420", "-1400", "-1380", "-1360", "-1340", "-1320", "-1300",
            "-1280", "-1260", "-1240", "-1220", "-1200", "-1180", "-1160",
            "-1140", "-1120", "-1100", "-1080", "-1060", "-1040", "-1020",
            "-1000", " -980", " -960", " -940", " -920", " -900", " -880",
            " -860", " -840", " -820", " -800", " -780", " -760", " -740",
            " -720", " -700", " -680", " -660", " -640", " -620", " -600",
            " -580", " -560", " -540", " -520", " -500", " -480", " -460",
            " -440", " -420", " -400", " -380", " -360", " -340", " -320",
            " -300", " -280", " -260", " -240", " -220", " -200", " -180",
            " -160", " -140", " -120", " -100", "  -80", "  -60", "  -40",
            "  -20", "    0", "  +20", "  +40", "  +60", "  +80", " +100",
            " +120", " +140", " +160", " +180", " +200", " +220", " +240",
            " +260", " +280", " +300", " +320", " +340", " +360", " +380",
            " +400", " +420", " +440", " +460", " +480", " +500", " +520",
            " +540", " +560", " +580", " +600", " +620", " +640", " +660",
            " +680", " +700", " +720", " +740", " +760", " +780", " +800",
            " +820", " +840", " +860", " +880", " +900", " +920", " +940",
            " +960", " +980", "+1000", "+1020", "+1040", "+1060", "+1080",
            "+1100", "+1120", "+1140", "+1160", "+1180", "+1200", "+1220",
            "+1240", "+1260", "+1280", "+1300", "+1320", "+1340", "+1360",
            "+1380", "+1400", "+1420", "+1440", "+1460", "+1480", "+1500",
            "+1520", "+1540", "+1560", "+1580", "+1600", "+1620", "+1640",
            "+1660", "+1680", "+1700", "+1720", "+1740", "+1760", "+1780",
            "+1800", "+1820", "+1840", "+1860", "+1880", "+1900", "+1920",
            "+1940", "+1960", "+1980", "+2000", "+2020", "+2040", "+2060",
            "+2080", "+2100", "+2120", "+2140", "+2160", "+2180", "+2200",
            "+2220", "+2240", "+2260", "+2280", "+2300", "+2320", "+2340",
            "+2360", "+2380", "+2400" };

    private final String[] fineOptions = new String[] {
            "-20", "-19", "-18", "-17", "-16", "-15", "-14", "-13", "-12",
            "-11", "-10", " -9", " -8", " -7", " -6", " -5", " -4", " -3",
            " -2", " -1", "  0", " +1", " +2", " +3", " +4", " +5", " +6",
            " +7", " +8", " +9", "+10", "+11", "+12", "+13", "+14", "+15",
            "+16", "+17", "+18", "+19", "+20" };

    private final String[] rateOptions = new String[] {
            " .2s", " .3s", " .4s", " .5s", " .6s", " .7s", " .8s", " .9s",
            "1.0s", "1.2s", "1.4s", "1.6s", "1.8s", "2.0s", "2.4s", "2.8s",
            "3.4s", "4.0s", "6.0s", "9.0s" };

    private final String[] revtypeOptions = new String[] {
            "Plate A", "Plate B", "Room A", "Room B", "Hall A", "Hall B",
            "Stadium", "Dual" };

    private LongtextScrollBarLookupWidget revLevelMain = null;
    private LongtextScrollBarLookupWidget revLevelSub = null;

    public RocktronIntellifexPresetEditor(Patch patch) {

        super("Rocktron Intellifex Single Editor", patch);

        // Global Pane
        JPanel globPane = new JPanel();
        globPane.setLayout(new GridBagLayout());

        addWidget(globPane, new PatchNameWidget(" Name  ", patch), 0, 0, 2, 1,
                0);

        IntellifexModel configType = new IntellifexModel(patch, 0);

        addWidget(globPane, new ComboBoxWidget("Configuration", patch,
                configType, null, configName), 0, 3, 2, 1, 3);

        globPane.setBorder(new TitledBorder(new EtchedBorder(
                EtchedBorder.RAISED), "Global", TitledBorder.CENTER,
                TitledBorder.CENTER));

        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.EAST;
        scrollPane.add(globPane, gbc);

        // Panels
        JPanel mixerPanel = createMixerPanel(patch);
        JPanel hushPanel = createHushPanel(patch);

        JPanel duckerPanel = createDuckerPanel(patch);
        JPanel reverbPanel = createReverbPanel(patch);

        if (mixerPanel != null) {
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 5;
            gbc.gridheight = 2;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.EAST;
            scrollPane.add(mixerPanel, gbc);
        }

        if (hushPanel != null) {
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.gridwidth = 5;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.EAST;
            scrollPane.add(hushPanel, gbc);
        }

        for (int i = 0; i < 8; i++) {
            JPanel vPanel = createVoiceDlyPanel(patch, i);
            if (vPanel != null) {
                gbc.gridx = 5;
                gbc.gridy = 3 + i;
                gbc.gridwidth = 5;
                gbc.gridheight = 1;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.anchor = GridBagConstraints.EAST;
                scrollPane.add(vPanel, gbc);
            }

        }

        if (duckerPanel != null) {
            gbc.gridx = 10;
            gbc.gridy = 3;
            gbc.gridwidth = 5;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.EAST;
            scrollPane.add(duckerPanel, gbc);
        }

        if (reverbPanel != null) {
            gbc.gridx = 10;
            gbc.gridy = 4;
            gbc.gridwidth = 5;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.EAST;
            scrollPane.add(reverbPanel, gbc);
        }

        // Add connection between duplicated reverb level sliders
        if (revLevelMain != null && revLevelSub != null) {
            revLevelMain.addEventListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    revLevelSub.setValue(revLevelMain.getValue());
                }
            });
            revLevelSub.addEventListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    revLevelMain.setValue(revLevelSub.getValue());
                }
            });
        }

        pack();
    }

    private int getLabelWidth(String s) {
        return (int) (new JLabel(s)).getPreferredSize().getWidth();
    }

    private JPanel createMixerPanel(Patch patch) {
        int labelWidth = getLabelWidth("Reverb Level  ");

        JPanel mixerPanel = new JPanel();
        mixerPanel.setLayout(new GridBagLayout());
        mixerPanel.setBorder(new TitledBorder(new EtchedBorder(
                EtchedBorder.RAISED), "Mixer", TitledBorder.CENTER,
                TitledBorder.CENTER));

        IntellifexModel configType = new IntellifexModel(patch, 0);

        switch (configType.get()) {
        case 0: // "Hush,Chorus,Delay,Reverb"
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "Effects Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 1), null, lvl6dbOptions), 0, 0,
                    1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "L Dir Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 2), null, lvl6dbOptions), 0, 5,
                    1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "R Dir Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 3), null, lvl6dbOptions), 0, 10,
                    1, 5, 0);
            addWidget(mixerPanel, new ComboBoxWidget("Direct Hush", patch,
                    new IntellifexModel(patch, 4), null, new String[] {
                            "Pre", "Post" }), 0, 15, 1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "Chorus Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 5), null, lvl0dbOptions), 0, 20,
                    1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "Delay Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 6), null, lvl0dbOptions), 0, 25,
                    1, 5, 0);
            revLevelMain =
                    new LongtextScrollBarLookupWidget("Reverb Level", patch, 0,
                            58, labelWidth, 5, new IntellifexModel(patch, 7),
                            null, lvl0dbOptions);
            addWidget(mixerPanel, revLevelMain, 0, 30, 1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget("Regen L",
                    patch, 0, 58, labelWidth, 5, new IntellifexModel(patch, 8),
                    null, lvl0dbOptions), 0, 35, 1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget("Regen R",
                    patch, 0, 58, labelWidth, 5, new IntellifexModel(patch, 9),
                    null, lvl0dbOptions), 0, 40, 1, 5, 0);
            break;
        case 1: // "Hush,PShift,Delay,Reverb"
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "Effects Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 1), null, lvl6dbOptions), 0, 0,
                    1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "L Dir Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 2), null, lvl6dbOptions), 0, 5,
                    1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "R Dir Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 3), null, lvl6dbOptions), 0, 10,
                    1, 5, 0);
            addWidget(mixerPanel, new ComboBoxWidget("Direct Hush", patch,
                    new IntellifexModel(patch, 4), null, new String[] {
                            "Pre", "Post" }), 0, 15, 1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "PShift Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 5), null, lvl0dbOptions), 0, 20,
                    1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "Delay Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 6), null, lvl0dbOptions), 0, 25,
                    1, 5, 0);
            revLevelMain =
                    new LongtextScrollBarLookupWidget("Reverb Level", patch, 0,
                            58, labelWidth, 5, new IntellifexModel(patch, 7),
                            null, lvl0dbOptions);
            addWidget(mixerPanel, revLevelMain, 0, 30, 1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget("Regen L",
                    patch, 0, 58, labelWidth, 5, new IntellifexModel(patch, 8),
                    null, lvl0dbOptions), 0, 35, 1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget("Regen R",
                    patch, 0, 58, labelWidth, 5, new IntellifexModel(patch, 9),
                    null, lvl0dbOptions), 0, 40, 1, 5, 0);
            break;
        case 2: // "Hush,Reverb"
            revLevelMain =
                    new LongtextScrollBarLookupWidget("Reverb Level", patch, 0,
                            58, labelWidth, 5, new IntellifexModel(patch, 1),
                            null, lvl6dbOptions);
            addWidget(mixerPanel, revLevelMain, 0, 0, 1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "L Dir Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 2), null, lvl6dbOptions), 0, 5,
                    1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "R Dir Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 3), null, lvl6dbOptions), 0, 10,
                    1, 5, 0);
            addWidget(mixerPanel, new ComboBoxWidget("Direct Hush", patch,
                    new IntellifexModel(patch, 4), null, new String[] {
                            "Pre", "Post" }), 0, 15, 1, 5, 0);
            break;
        case 3: // "Hush,Delay,Ducker"
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "Delay Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 1), null, lvl6dbOptions), 0, 0,
                    1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "L Dir Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 2), null, lvl6dbOptions), 0, 5,
                    1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "R Dir Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 3), null, lvl6dbOptions), 0, 10,
                    1, 5, 0);
            addWidget(mixerPanel, new ComboBoxWidget("Direct Hush", patch,
                    new IntellifexModel(patch, 4), null, new String[] {
                            "Pre", "Post" }), 0, 15, 1, 5, 0);
            break;
        case 4: // "Hush,8V-Chorus,Delay"
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "Chorus Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 1), null, lvl6dbOptions), 0, 0,
                    1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "L Dir Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 2), null, lvl6dbOptions), 0, 5,
                    1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "R Dir Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 3), null, lvl6dbOptions), 0, 10,
                    1, 5, 0);
            addWidget(mixerPanel, new ComboBoxWidget("Direct Hush", patch,
                    new IntellifexModel(patch, 4), null, new String[] {
                            "Pre", "Post" }), 0, 15, 1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget("Regen L",
                    patch, 0, 58, labelWidth, 5, new IntellifexModel(patch, 5),
                    null, lvl0dbOptions), 0, 20, 1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget("Regen R",
                    patch, 0, 58, labelWidth, 5, new IntellifexModel(patch, 6),
                    null, lvl0dbOptions), 0, 25, 1, 5, 0);
            break;
        case 5: // "Hush,PShift,Delay"
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "PShift Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 1), null, lvl6dbOptions), 0, 0,
                    1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "L Dir Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 2), null, lvl6dbOptions), 0, 5,
                    1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget(
                    "R Dir Level", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 3), null, lvl6dbOptions), 0, 10,
                    1, 5, 0);
            addWidget(mixerPanel, new ComboBoxWidget("Direct Hush", patch,
                    new IntellifexModel(patch, 4), null, new String[] {
                            "Pre", "Post" }), 0, 15, 1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget("Regen L",
                    patch, 0, 58, labelWidth, 5, new IntellifexModel(patch, 5),
                    null, lvl0dbOptions), 0, 20, 1, 5, 0);
            addWidget(mixerPanel, new LongtextScrollBarLookupWidget("Regen R",
                    patch, 0, 58, labelWidth, 5, new IntellifexModel(patch, 6),
                    null, lvl0dbOptions), 0, 25, 1, 5, 0);
            break;
        default:
            break;

        }
        return mixerPanel;
    }

    private JPanel createHushPanel(Patch patch) {
        int labelWidth = getLabelWidth("Exp Thresh  ");

        JPanel hushPanel = new JPanel();
        hushPanel.setLayout(new GridBagLayout());
        hushPanel.setBorder(new TitledBorder(new EtchedBorder(
                EtchedBorder.RAISED), "Hush", TitledBorder.CENTER,
                TitledBorder.CENTER));

        IntellifexModel configType = new IntellifexModel(patch, 0);

        switch (configType.get()) {
        case 0: // "Hush,Chorus,Delay,Reverb"
            addWidget(hushPanel, new ComboBoxWidget("Hush I/O", patch,
                    new IntellifexModel(patch, 10), null, new String[] {
                            "Out", "In" }), 0, 0, 1, 5, 0);
            addWidget(hushPanel, new LongtextScrollBarLookupWidget(
                    "Exp Thresh", patch, 0, 24, labelWidth, 5,
                    new IntellifexModel(patch, 11), null, sens_92_20Options),
                    0, 5, 1, 5, 0);
            addWidget(hushPanel, new LongtextScrollBarLookupWidget("Rel Rate",
                    patch, 0, 9, labelWidth, 5, new IntellifexModel(patch, 12),
                    null, relTimeOptions), 0, 10, 1, 5, 0);
            break;
        case 1: // "Hush,PShift,Delay,Reverb"
            addWidget(hushPanel, new ComboBoxWidget("Hush I/O", patch,
                    new IntellifexModel(patch, 10), null, new String[] {
                            "Out", "In" }), 0, 0, 1, 5, 0);
            addWidget(hushPanel, new LongtextScrollBarLookupWidget(
                    "Exp Thresh", patch, 0, 24, labelWidth, 5,
                    new IntellifexModel(patch, 11), null, sens_92_20Options),
                    0, 5, 1, 5, 0);
            addWidget(hushPanel, new LongtextScrollBarLookupWidget("Rel Rate",
                    patch, 0, 9, labelWidth, 5, new IntellifexModel(patch, 12),
                    null, relTimeOptions), 0, 10, 1, 5, 0);
            break;
        case 2: // "Hush,Reverb"
            addWidget(hushPanel, new ComboBoxWidget("Hush I/O", patch,
                    new IntellifexModel(patch, 5), null, new String[] {
                            "Out", "In" }), 0, 0, 1, 5, 0);
            addWidget(hushPanel, new LongtextScrollBarLookupWidget(
                    "Exp Thresh", patch, 0, 24, labelWidth, 5,
                    new IntellifexModel(patch, 6), null, sens_92_20Options), 0,
                    5, 1, 5, 0);
            addWidget(hushPanel, new LongtextScrollBarLookupWidget("Rel Rate",
                    patch, 0, 9, labelWidth, 5, new IntellifexModel(patch, 7),
                    null, relTimeOptions), 0, 10, 1, 5, 0);
            break;
        case 3: // "Hush,Delay,Ducker"
            addWidget(hushPanel, new ComboBoxWidget("Hush I/O", patch,
                    new IntellifexModel(patch, 5), null, new String[] {
                            "Out", "In" }), 0, 0, 1, 5, 0);
            addWidget(hushPanel, new LongtextScrollBarLookupWidget(
                    "Exp Thresh", patch, 0, 24, labelWidth, 5,
                    new IntellifexModel(patch, 6), null, sens_92_20Options), 0,
                    5, 1, 5, 0);
            addWidget(hushPanel, new LongtextScrollBarLookupWidget("Rel Rate",
                    patch, 0, 9, labelWidth, 5, new IntellifexModel(patch, 7),
                    null, relTimeOptions), 0, 10, 1, 5, 0);
            break;
        case 4: // "Hush,8V-Chorus,Delay"
            addWidget(hushPanel, new ComboBoxWidget("Hush I/O", patch,
                    new IntellifexModel(patch, 7), null, new String[] {
                            "Out", "In" }), 0, 0, 1, 5, 0);
            addWidget(hushPanel, new LongtextScrollBarLookupWidget(
                    "Exp Thresh", patch, 0, 24, labelWidth, 5,
                    new IntellifexModel(patch, 8), null, sens_92_20Options), 0,
                    5, 1, 5, 0);
            addWidget(hushPanel, new LongtextScrollBarLookupWidget("Rel Rate",
                    patch, 0, 9, labelWidth, 5, new IntellifexModel(patch, 9),
                    null, relTimeOptions), 0, 10, 1, 5, 0);
            break;
        case 5: // "Hush,PShift,Delay"
            addWidget(hushPanel, new ComboBoxWidget("Hush I/O", patch,
                    new IntellifexModel(patch, 7), null, new String[] {
                            "Out", "In" }), 0, 0, 1, 5, 0);
            addWidget(hushPanel, new LongtextScrollBarLookupWidget(
                    "Exp Thresh", patch, 0, 24, labelWidth, 5,
                    new IntellifexModel(patch, 8), null, sens_92_20Options), 0,
                    5, 1, 5, 0);
            addWidget(hushPanel, new LongtextScrollBarLookupWidget("Rel Rate",
                    patch, 0, 9, labelWidth, 5, new IntellifexModel(patch, 9),
                    null, relTimeOptions), 0, 10, 1, 5, 0);
            break;
        default:
            break;

        }
        return hushPanel;
    }

    private JPanel createVoiceDlyPanel(Patch patch, int voiceNo) {
        int labelWidth = getLabelWidth("Delay HF Damp  ");

        IntellifexModel configType = new IntellifexModel(patch, 0);

        JPanel voicedlyPanel = new JPanel();
        voicedlyPanel.setLayout(new GridBagLayout());

        String borderTxt = "";

        switch (configType.get()) {
        case 0: // "Hush,Chorus,Delay,Reverb"
            if (voiceNo > 3)
                return null;
            borderTxt = "Chorus Voice/Dly ".concat(String.valueOf(voiceNo + 1));

            addWidget(
                    voicedlyPanel,
                    new LongtextScrollBarLookupWidget("Level ".concat(String
                            .valueOf(voiceNo + 1)), patch, 0, 58, labelWidth,
                            5, new IntellifexModel(patch, 13 + 5 * voiceNo),
                            null, lvl0dbOptions), 0, 5, 1, 5, 0);
            addWidget(
                    voicedlyPanel,
                    new LongtextScrollBarLookupWidget("Pan ".concat(String
                            .valueOf(voiceNo + 1)), patch, 0, 100, labelWidth,
                            5, new IntellifexModel(patch, 14 + 5 * voiceNo),
                            null, panOptions), 0, 15, 1, 5, 0);
            addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget("Delay",
                    patch, 0, 209, labelWidth, 5, new IntellifexModel(patch,
                            15 + 5 * voiceNo), null, delayOptions), 0, 20, 1,
                    5, 0);
            addWidget(
                    voicedlyPanel,
                    new LongtextScrollBarWidget("Depth ".concat(String
                            .valueOf(voiceNo + 1)), patch, 0, 100, 0,
                            labelWidth, 5, new IntellifexModel(patch,
                                    16 + 5 * voiceNo), null), 0, 25, 1, 5, 0);
            addWidget(
                    voicedlyPanel,
                    new LongtextScrollBarWidget("Rate ".concat(String
                            .valueOf(voiceNo + 1)), patch, 0, 254, 0,
                            labelWidth, 5, new IntellifexModel(patch,
                                    17 + 5 * voiceNo), null), 0, 30, 1, 5, 0);
            break;
        case 1: // "Hush,PShift,Delay,Reverb"
            if (voiceNo > 2)
                return null;
            if (voiceNo < 2) {
                borderTxt = "Voice/Dly ".concat(String.valueOf(voiceNo + 1));

                addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget(
                        "Pitch ".concat(String.valueOf(voiceNo + 1)), patch, 0,
                        240, labelWidth, 5, new IntellifexModel(patch,
                                13 + 5 * voiceNo), null, pitchOptions), 0, 0,
                        1, 5, 0);
                addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget(
                        "Fine ".concat(String.valueOf(voiceNo + 1)), patch, 0,
                        40, labelWidth, 5, new IntellifexModel(patch,
                                14 + 5 * voiceNo), null, fineOptions), 0, 5, 1,
                        5, 0);
                addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget(
                        "Level ".concat(String.valueOf(voiceNo + 1)), patch, 0,
                        58, labelWidth, 5, new IntellifexModel(patch,
                                15 + 5 * voiceNo), null, lvl0dbOptions), 0, 10,
                        1, 5, 0);
                addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget(
                        "Pan ".concat(String.valueOf(voiceNo + 1)), patch, 0,
                        100, labelWidth, 5, new IntellifexModel(patch,
                                16 + 5 * voiceNo), null, panOptions), 0, 15, 1,
                        5, 0);
                addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget(
                        "Delay".concat(String.valueOf(voiceNo + 1)), patch, 0,
                        209, labelWidth, 5, new IntellifexModel(patch,
                                17 + 5 * voiceNo), null, delayOptions), 0, 20,
                        1, 5, 0);
            } else // voiceNo==2
            {
                borderTxt = "Delay ".concat(String.valueOf(voiceNo + 1));

                addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget(
                        "Level ".concat(String.valueOf(voiceNo + 1)), patch, 0,
                        58, labelWidth, 5, new IntellifexModel(patch, 23),
                        null, lvl0dbOptions), 0, 0, 1, 5, 0);
                addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget(
                        "Pan ".concat(String.valueOf(voiceNo + 1)), patch, 0,
                        100, labelWidth, 5, new IntellifexModel(patch, 24),
                        null, panOptions), 0, 5, 1, 5, 0);
                addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget(
                        "Delay", patch, 0, 209, labelWidth, 5,
                        new IntellifexModel(patch, 25), null, delayOptions), 0,
                        10, 1, 5, 0);
            }

            break;
        case 2: // "Hush,Reverb"
            return null;
        case 3: // "Hush,Delay,Ducker"
            if (voiceNo > 0)
                return null;
            borderTxt = "Delay";

            // get delay type for selecting between fine/coarse delay time step
            IntellifexModel delayType = new IntellifexModel(patch, 18);

            addWidget(voicedlyPanel, new ComboBoxWidget("Delay", patch,
                    new IntellifexModel(patch, 8), null, new String[] {
                            "Muted", "Active" }), 0, 0, 1, 5, 0);

            addWidget(voicedlyPanel, new ComboBoxWidget("Mute Type", patch,
                    new IntellifexModel(patch, 9), null, new String[] {
                            "Pre", "Post", "Both" }), 0, 5, 1, 5, 0);

            addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget(
                    "Level 1", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 10), null, lvl0dbOptions), 0,
                    10, 1, 5, 0);
            addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget("Pan 1",
                    patch, 0, 100, labelWidth, 5,
                    new IntellifexModel(patch, 11), null, panOptions), 0, 15,
                    1, 5, 0);

            final LongtextScrollBarLookupWidget timeWidget1 =
                    new LongtextScrollBarLookupWidget("Delay Time 1", patch, 0,
                            150, labelWidth, 5, new IntellifexModel(patch, 12),
                            null, delayType.get() == 2 ? coarseDelayOptions
                                    : fineDelayOptions);
            addWidget(voicedlyPanel, timeWidget1, 0, 20, 1, 5, 0);
            addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget(
                    "Regen 1", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 13), null, lvl0dbOptions), 0,
                    25, 1, 5, 0);

            addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget(
                    "Level 2", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 14), null, lvl0dbOptions), 0,
                    30, 1, 5, 0);
            addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget("Pan 2",
                    patch, 0, 100, labelWidth, 5,
                    new IntellifexModel(patch, 15), null, panOptions), 0, 35,
                    1, 5, 0);

            final LongtextScrollBarLookupWidget timeWidget2 =
                    new LongtextScrollBarLookupWidget("Delay Time 2", patch, 0,
                            150, labelWidth, 5, new IntellifexModel(patch, 16),
                            null, delayType.get() == 2 ? coarseDelayOptions
                                    : fineDelayOptions);
            addWidget(voicedlyPanel, timeWidget2, 0, 40, 1, 5, 0);
            addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget(
                    "Regen 2", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 17), null, lvl0dbOptions), 0,
                    45, 1, 5, 0);

            final ComboBoxWidget delayTypeWidget =
                    new ComboBoxWidget("Delay Type", patch,
                            new IntellifexModel(patch, 18), null, new String[] {
                                    "Stereo", "Ping-Pong", "2-Tap" });
            addWidget(voicedlyPanel, delayTypeWidget, 0, 50, 1, 5, 0);

            delayTypeWidget.addEventListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getItem() == "2-Tap") {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            timeWidget1.changeOptions(fineDelayOptions);
                            timeWidget2.changeOptions(fineDelayOptions);
                        } else if (e.getStateChange() == ItemEvent.SELECTED) {
                            timeWidget1.changeOptions(coarseDelayOptions);
                            timeWidget2.changeOptions(coarseDelayOptions);
                        }
                    }
                }
            });

            addWidget(voicedlyPanel, new LongtextScrollBarWidget(
                    "Delay HF Damp", patch, 0, 99, 0, labelWidth, 5,
                    new IntellifexModel(patch, 19), null), 0, 55, 1, 5, 0);

            break;
        case 4: // "Hush,8V-Chorus,Delay"
            borderTxt = "Voice/Dly ".concat(String.valueOf(voiceNo + 1));

            addWidget(
                    voicedlyPanel,
                    new LongtextScrollBarLookupWidget("Level ".concat(String
                            .valueOf(voiceNo + 1)), patch, 0, 58, labelWidth,
                            5, new IntellifexModel(patch, 10 + 5 * voiceNo),
                            null, lvl0dbOptions), 0, 5, 1, 5, 0);
            addWidget(
                    voicedlyPanel,
                    new LongtextScrollBarLookupWidget("Pan ".concat(String
                            .valueOf(voiceNo + 1)), patch, 0, 100, labelWidth,
                            5, new IntellifexModel(patch, 11 + 5 * voiceNo),
                            null, panOptions), 0, 15, 1, 5, 0);
            addWidget(voicedlyPanel, new LongtextScrollBarLookupWidget("Delay",
                    patch, 0, 209, labelWidth, 5, new IntellifexModel(patch,
                            12 + 5 * voiceNo), null, delayOptions), 0, 20, 1,
                    5, 0);
            addWidget(
                    voicedlyPanel,
                    new LongtextScrollBarWidget("Depth ".concat(String
                            .valueOf(voiceNo + 1)), patch, 0, 100, 0,
                            labelWidth, 5, new IntellifexModel(patch,
                                    13 + 5 * voiceNo), null), 0, 25, 1, 5, 0);
            addWidget(
                    voicedlyPanel,
                    new LongtextScrollBarWidget("Rate ".concat(String
                            .valueOf(voiceNo + 1)), patch, 0, 254, 0,
                            labelWidth, 5, new IntellifexModel(patch,
                                    14 + 5 * voiceNo), null), 0, 30, 1, 5, 0);

            break;
        case 5: // "Hush,PShift,Delay"
            if (voiceNo > 3)
                return null;
            borderTxt = "Voice ".concat(String.valueOf(voiceNo + 1));

            addWidget(
                    voicedlyPanel,
                    new LongtextScrollBarLookupWidget("Pitch ".concat(String
                            .valueOf(voiceNo + 1)), patch, 0, 240, labelWidth,
                            5, new IntellifexModel(patch, 10 + 5 * voiceNo),
                            null, pitchOptions), 0, 0, 1, 5, 0);
            addWidget(
                    voicedlyPanel,
                    new LongtextScrollBarLookupWidget("Fine ".concat(String
                            .valueOf(voiceNo + 1)), patch, 0, 40, labelWidth,
                            5, new IntellifexModel(patch, 11 + 5 * voiceNo),
                            null, fineOptions), 0, 5, 1, 5, 0);
            addWidget(
                    voicedlyPanel,
                    new LongtextScrollBarLookupWidget("Level ".concat(String
                            .valueOf(voiceNo + 1)), patch, 0, 58, labelWidth,
                            5, new IntellifexModel(patch, 12 + 5 * voiceNo),
                            null, lvl0dbOptions), 0, 10, 1, 5, 0);
            addWidget(
                    voicedlyPanel,
                    new LongtextScrollBarLookupWidget("Pan ".concat(String
                            .valueOf(voiceNo + 1)), patch, 0, 100, labelWidth,
                            5, new IntellifexModel(patch, 13 + 5 * voiceNo),
                            null, panOptions), 0, 15, 1, 5, 0);
            addWidget(
                    voicedlyPanel,
                    new LongtextScrollBarLookupWidget("Delay".concat(String
                            .valueOf(voiceNo + 1)), patch, 0, 209, labelWidth,
                            5, new IntellifexModel(patch, 14 + 5 * voiceNo),
                            null, delayOptions), 0, 20, 1, 5, 0);

            break;
        default:
            break;

        }

        voicedlyPanel.setBorder(new TitledBorder(new EtchedBorder(
                EtchedBorder.RAISED), borderTxt, TitledBorder.CENTER,
                TitledBorder.CENTER));

        return voicedlyPanel;
    }

    private JPanel createDuckerPanel(Patch patch) {
        int labelWidth = getLabelWidth("Release Rate  ");

        IntellifexModel configType = new IntellifexModel(patch, 0);

        JPanel duckerPanel = new JPanel();
        duckerPanel.setLayout(new GridBagLayout());
        duckerPanel.setBorder(new TitledBorder(new EtchedBorder(
                EtchedBorder.RAISED), "Ducker", TitledBorder.CENTER,
                TitledBorder.CENTER));

        switch (configType.get()) {
        case 0: // "Hush,Chorus,Delay,Reverb"
            addWidget(duckerPanel, new ComboBoxWidget("Ducker", patch,
                    new IntellifexModel(patch, 33), null, new String[] {
                            "Off", "Delay", "Reverb", "Both" }), 0, 0, 1, 5, 0);
            addWidget(duckerPanel, new LongtextScrollBarLookupWidget(
                    "Sensitivity", patch, 0, 24, labelWidth, 5,
                    new IntellifexModel(patch, 34), null, sens_92_20Options),
                    0, 5, 1, 5, 0);
            addWidget(duckerPanel, new LongtextScrollBarLookupWidget(
                    "Attenuation", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 35), null, lvl0dbOptions), 0,
                    10, 1, 5, 0);
            addWidget(duckerPanel, new LongtextScrollBarLookupWidget(
                    "Release Rate", patch, 0, 19, labelWidth, 5,
                    new IntellifexModel(patch, 36), null, rateOptions), 0, 15,
                    1, 5, 0);
            break;
        case 1: // "Hush,PShift,Delay,Reverb"
            addWidget(duckerPanel, new ComboBoxWidget("Ducker", patch,
                    new IntellifexModel(patch, 26), null, new String[] {
                            "Off", "Delay", "Reverb", "Both" }), 0, 0, 1, 5, 0);
            addWidget(duckerPanel, new LongtextScrollBarLookupWidget(
                    "Sensitivity", patch, 0, 24, labelWidth, 5,
                    new IntellifexModel(patch, 27), null, sens_92_20Options),
                    0, 5, 1, 5, 0);
            addWidget(duckerPanel, new LongtextScrollBarLookupWidget(
                    "Attenuation", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 28), null, lvl0dbOptions), 0,
                    10, 1, 5, 0);
            addWidget(duckerPanel, new LongtextScrollBarLookupWidget(
                    "Release Rate", patch, 0, 19, labelWidth, 5,
                    new IntellifexModel(patch, 29), null, rateOptions), 0, 15,
                    1, 5, 0);
            break;
        case 2: // "Hush,Reverb"
            return null;
        case 3: // "Hush,Delay,Ducker"
            addWidget(duckerPanel, new ComboBoxWidget("Ducker", patch,
                    new IntellifexModel(patch, 20), null, new String[] {
                            "Off", "On" }), 0, 0, 1, 5, 0);
            addWidget(duckerPanel, new LongtextScrollBarLookupWidget(
                    "Sensitivity", patch, 0, 24, labelWidth, 5,
                    new IntellifexModel(patch, 21), null, sens_92_20Options),
                    0, 5, 1, 5, 0);
            addWidget(duckerPanel, new LongtextScrollBarLookupWidget(
                    "Attenuation", patch, 0, 58, labelWidth, 5,
                    new IntellifexModel(patch, 22), null, lvl0dbOptions), 0,
                    10, 1, 5, 0);
            addWidget(duckerPanel, new LongtextScrollBarLookupWidget(
                    "Release Rate", patch, 0, 19, labelWidth, 5,
                    new IntellifexModel(patch, 23), null, rateOptions), 0, 15,
                    1, 5, 0);
            break;
        case 4: // "Hush,8V-Chorus,Delay"
            return null;
        case 5: // "Hush,PShift,Delay"
            return null;
        default:
            break;

        }
        return duckerPanel;
    }

    private JPanel createReverbPanel(Patch patch) {
        int labelWidth = getLabelWidth("Reverb HF Damp  ");

        IntellifexModel configType = new IntellifexModel(patch, 0);

        JPanel reverbPanel = new JPanel();
        reverbPanel.setLayout(new GridBagLayout());
        reverbPanel.setBorder(new TitledBorder(new EtchedBorder(
                EtchedBorder.RAISED), "Reverb", TitledBorder.CENTER,
                TitledBorder.CENTER));

        switch (configType.get()) {
        case 0: // "Hush,Chorus,Delay,Reverb"
            addWidget(reverbPanel, new ComboBoxWidget("Rev Input", patch,
                    new IntellifexModel(patch, 37), null, new String[] {
                            "Muted", "Active" }), 0, 0, 1, 5, 0);
            addWidget(reverbPanel, new LongtextScrollBarLookupWidget(
                    "Dir In Pan", patch, 0, 100, labelWidth, 6,
                    new IntellifexModel(patch, 38), null, panOptions), 0, 5, 1,
                    5, 0);
            addWidget(reverbPanel, new LongtextScrollBarLookupWidget(
                    "Mix Dir/Dly", patch, 0, 100, labelWidth, 6,
                    new IntellifexModel(patch, 39), null, dirdlymixOptions), 0,
                    15, 1, 5, 0);

            revLevelSub =
                    new LongtextScrollBarLookupWidget("Reverb Level", patch, 0,
                            58, labelWidth, 6, new IntellifexModel(patch, 7),
                            null, lvl0dbOptions);
            addWidget(reverbPanel, revLevelSub, 0, 20, 1, 5, 0);

            addWidget(reverbPanel, new LongtextScrollBarWidget("Reverb Decay",
                    patch, 0, 99, 0, labelWidth, 6, new IntellifexModel(patch,
                            40), null), 0, 25, 1, 5, 0);
            addWidget(reverbPanel, new LongtextScrollBarWidget(
                    "Reverb HF Damp", patch, 0, 99, 0, labelWidth, 6,
                    new IntellifexModel(patch, 41), null), 0, 30, 1, 5, 0);
            break;
        case 1: // "Hush,PShift,Delay,Reverb"
            addWidget(reverbPanel, new ComboBoxWidget("Rev Input", patch,
                    new IntellifexModel(patch, 30), null, new String[] {
                            "Muted", "Active" }), 0, 0, 1, 5, 0);
            addWidget(reverbPanel, new LongtextScrollBarLookupWidget(
                    "Dir In Pan", patch, 0, 100, labelWidth, 6,
                    new IntellifexModel(patch, 31), null, panOptions), 0, 5, 1,
                    5, 0);
            addWidget(reverbPanel, new LongtextScrollBarLookupWidget(
                    "Mix Dir/Dly", patch, 0, 100, labelWidth, 6,
                    new IntellifexModel(patch, 32), null, dirdlymixOptions), 0,
                    15, 1, 5, 0);

            revLevelSub =
                    new LongtextScrollBarLookupWidget("Reverb Level", patch, 0,
                            58, labelWidth, 6, new IntellifexModel(patch, 7),
                            null, lvl0dbOptions);
            addWidget(reverbPanel, revLevelSub, 0, 20, 1, 5, 0);

            addWidget(reverbPanel, new LongtextScrollBarWidget("Reverb Decay",
                    patch, 0, 99, 0, labelWidth, 6, new IntellifexModel(patch,
                            33), null), 0, 25, 1, 5, 0);
            addWidget(reverbPanel, new LongtextScrollBarWidget(
                    "Reverb HF Damp", patch, 0, 99, 0, labelWidth, 6,
                    new IntellifexModel(patch, 34), null), 0, 30, 1, 5, 0);
            break;
        case 2: // "Hush,Reverb"

            revLevelSub =
                    new LongtextScrollBarLookupWidget("Reverb Level", patch, 0,
                            58, labelWidth, 5, new IntellifexModel(patch, 1),
                            null, lvl6dbOptions);
            addWidget(reverbPanel, revLevelSub, 0, 0, 1, 5, 0);

            addWidget(reverbPanel, new LongtextScrollBarWidget("Reverb Decay",
                    patch, 0, 99, 0, labelWidth, 5, new IntellifexModel(patch,
                            8), null), 0, 5, 1, 5, 0);
            addWidget(reverbPanel, new LongtextScrollBarWidget(
                    "Reverb HF Damp", patch, 0, 99, 0, labelWidth, 5,
                    new IntellifexModel(patch, 9), null), 0, 10, 1, 5, 0);
            addWidget(reverbPanel, new LongtextScrollBarWidget("Low Freq",
                    patch, 0, 99, 0, labelWidth, 5, new IntellifexModel(patch,
                            10), null), 0, 15, 1, 5, 0);
            addWidget(reverbPanel, new ComboBoxWidget("Reverb Type", patch,
                    new IntellifexModel(patch, 11), null, revtypeOptions), 0,
                    20, 1, 5, 0);
            addWidget(reverbPanel, new LongtextScrollBarLookupWidget(
                    "Direct In Pan", patch, 0, 100, labelWidth, 5,
                    new IntellifexModel(patch, 12), null, panOptions), 0, 25,
                    1, 5, 0);
            addWidget(reverbPanel, new LongtextScrollBarLookupWidget(
                    "PreDelay L", patch, 0, 209, labelWidth, 5,
                    new IntellifexModel(patch, 13), null, predelayOptions), 0,
                    30, 1, 5, 0);
            addWidget(reverbPanel, new LongtextScrollBarLookupWidget(
                    "PreDelay R", patch, 0, 209, labelWidth, 5,
                    new IntellifexModel(patch, 14), null, predelayOptions), 0,
                    35, 1, 5, 0);
            addWidget(reverbPanel, new ComboBoxWidget("Gate", patch,
                    new IntellifexModel(patch, 15), null, new String[] {
                            "Off", "On" }), 0, 40, 1, 5, 0);
            addWidget(reverbPanel, new LongtextScrollBarWidget("Gate Decay",
                    patch, 0, 31, 0, labelWidth, 5, new IntellifexModel(patch,
                            16), null), 0, 45, 1, 5, 0);
            addWidget(reverbPanel, new LongtextScrollBarLookupWidget(
                    "Gate Thresh", patch, 0, 24, labelWidth, 5,
                    new IntellifexModel(patch, 17), null, sens_92_20Options),
                    0, 50, 1, 5, 0);
            addWidget(reverbPanel, new LongtextScrollBarWidget("Hold Time",
                    patch, 0, 99, 0, labelWidth, 5, new IntellifexModel(patch,
                            18), null), 0, 55, 1, 5, 0);
            break;
        case 3: // "Hush,Delay,Ducker"
            return null;
        case 4: // "Hush,8V-Chorus,Delay"
            return null;
        case 5: // "Hush,PShift,Delay"
            return null;
        default:
            break;

        }
        return reverbPanel;
    }

}
