package recipient.application_service

import com.sansan.billone.lib.ApplicationResult
import com.sansan.billone.lib.DomainEventContext
import io.ktor.http.content.*
import recipient.domain.invoice.*
import recipient.domain.recipient.RecipientUUID
import recipient.domain.tenant.TenantNameId
import recipient.util.runInTransaction
import java.util.*

object InvoiceUploadService {
    fun upload(
        tenantNameId: TenantNameId,
        recipientUUID: RecipientUUID,
        multipartData: MultiPartData,
        domainEventContext: DomainEventContext,
    ): ApplicationResult {
        //  TODO: 課題4
         val invoice = Invoice(
            InvoiceUUID(UUID.randomUUID().toString()),
            recipientUUID,
            InvoiceFile(multipartData),
            InvoiceStatus.UPLOADED
        )

        return runInTransaction(tenantNameId, domainEventContext) { handle ->
            InvoiceRepository.save(invoice, handle)
            ApplicationResult.Success
        }
    }
}
