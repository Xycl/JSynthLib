package org.jsynthlib.device.model;

public class DeviceException extends Exception {

    private static final long serialVersionUID = 1L;

    public DeviceException() {
        super();
    }

    public DeviceException(String arg0, Throwable arg1, boolean arg2,
            boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public DeviceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeviceException(String message) {
        super(message);
    }

    public DeviceException(Throwable cause) {
        super(cause);
    }

}
