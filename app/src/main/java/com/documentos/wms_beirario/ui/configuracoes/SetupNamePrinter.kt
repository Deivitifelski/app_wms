package com.documentos.wms_beirario.ui.configuracoes

class SetupNamePrinter {
    companion object {
        var applicationToken = ""
        var mNamePrinterString = ""
        var mSpeed = ""
        var mTemp = ""
        var screenImpressoras = SetupNamePrinter::class.java
        var zplTest = "                     ^XA\n" +
                "^FO50,50^GB700,3,3^FS\n" +
                "\n" +
                "^FX Second section with recipient address and permit information.\n" +
                "^CFA,30\n" +
                "^FO50,100^FDIMPRIMIU CORRETAMENTE^FS\n" +
                "^FO50,500^GB700,3,3^FS\n" +
                "\n" +
                "^FX Third section with bar code.\n" +
                "^BY5,2,270\n" +
                "^FO100,550^BC^FD12345678^FS\n" +
                "\n" +
                "^FX Fourth section (the two boxes on the bottom).\n" +
                "^FO50,900^GB700,250,3^FS\n" +
                "^FO400,900^GB3,250,3^FS\n" +
                "^CF0,40\n" +
                "^FO100,960^FDCtr. X34B-1^FS\n" +
                "^FO100,1010^FDREF1 F00B47^FS\n" +
                "^FO100,1060^FDREF2 BL4H8^FS\n" +
                "^CF0,190\n" +
                "^FO470,955^FDCA^FS\n" +
                "^XZ"
    }
}