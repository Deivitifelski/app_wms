import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode2
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode3
import com.documentos.wms_beirario.model.recebimento.request.PostReciptQrCode1

class ReceiptRepository() {
    private fun getServ() = RetrofitClient().getClient()

    //01
    suspend fun receiptPost1(
        postDocumentoRequestRec1: PostReciptQrCode1,
        idArmazem: Int,
        token: String
    ) =
        getServ().receiptPost1(
            postDocumentoRequestRec1 = postDocumentoRequestRec1,
            idArmazem = idArmazem,
            token = token
        )

    //02
    suspend fun receiptPost2(
        idTarefa: String,
        postReceiptQrCode2: PostReceiptQrCode2,
        idArmazem: Int,
        token: String
    ) =
        getServ().receiptPointed2(
            idTarefa = idTarefa,
            postReceiptQrCode2 = postReceiptQrCode2,
            idArmazem = idArmazem,
            token = token
        )

    //03
    suspend fun receiptPost3(
        idTarefa: String,
        postReceiptQrCode3: PostReceiptQrCode3,
        idArmazem: Int,
        token: String
    ) =
        getServ().receipt3(
            idTarefa = idTarefa, postReceiptQrCode3 = postReceiptQrCode3, idArmazem = idArmazem,
            token = token
        )
}