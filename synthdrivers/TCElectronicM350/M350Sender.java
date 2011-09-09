package synthdrivers.TCElectronicM350;

import core.SysexSender;

class M350Sender extends SysexSender {

	//type 22 parameter change:  f0 00 20 1F 00 58 22 01 xx yy yy F7
    private byte[] b = {
            (byte) 0xF0, 0x00, 0x20, 0x1f, 0x00, 0x58, 0x22, 0x01,
             0, 0, 0, (byte) 0xF7           
    };

    public M350Sender(int parameter) {
        b[8] = (byte) parameter;
    }

    public byte[] generate(int value) {
        b[4] = (byte) (channel - 1);
        if (b[8] == 13)
        {//tap is only param that uses both bytes
        	b[9] = (byte) (value % 128);
        	b[10] = (byte) (value / 128);
        }
        else
        {
        	b[9] = (byte) value;
        	b[10] = (byte) 0;
        }
        return b;
    }
}
