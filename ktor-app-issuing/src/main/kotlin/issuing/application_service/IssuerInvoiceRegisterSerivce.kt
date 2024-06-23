package issuing.application_service

import com.sansan.billone.lib.ApplicationResult
import com.sansan.billone.lib.DomainEventContext
import issuing.controller.IssuerInvoiceCreateArgs
import issuing.domain.issuer.IssuerUUID
import issuing.domain.tenant.TenantNameId
import issuing.domain.issuer_invoice.IssuerInvoice
import issuing.domain_event.DomainEventRepository
import issuing.domain_event.IssuerInvoicePublished
import issuing.util.runInTransaction
import issuing.infrastructure.IssuerInvoiceRepository
import issuing.infrastructure.IssuerRepository
import issuing.infrastructure.RecipientRepository

object IssuerInvoiceRegisterService {
    fun register(
        args: IssuerInvoiceCreateArgs,
        issuerUUID: IssuerUUID,
        tenantNameId: TenantNameId,
        domainEventContext: DomainEventContext,
    ): ApplicationResult {
        return runInTransaction(tenantNameId, domainEventContext) { handle ->
            //  TODO: 課題3

            val issuer =
                IssuerRepository.getOrNull(tenantNameId, issuerUUID, handle)
                    ?: return@runInTransaction ApplicationResult.Failure("Issuer not found")
            val recipient =
                RecipientRepository.getOrNUll(args.recipientUUID, handle)
                    ?: return@runInTransaction ApplicationResult.Failure("Recipient Not Found")

            // issuerInvoiceをつくる
            val issuerInvoice = IssuerInvoice.create(
                tenantNameId = tenantNameId,
                supplierTenantNameId = recipient.tenantNameId,
                issuerUUID = issuerUUID,
                invoiceAmount = args.invoiceAmount,
                paymentDeadline = args.paymentDeadline,
                recipientUUID = args.recipientUUID,
            )

            IssuerInvoiceRepository.register(issuerInvoice, handle)

            // ドメインイベントを発行
            val issuerInvoiceRegistered = IssuerInvoicePublished(
                tenantNameId = tenantNameId.value,
                issuerInvoiceUUID = issuerInvoice.issuerInvoiceUUID,
                issuerUUID = issuerUUID,
                recipientUUID = args.recipientUUID,
                issuerName = issuer.issuerName.value,
                issuerOrganizationName = issuer.issuerEmail.value,
                recipientName = recipient.recipientName.value,
                recipientOrganizationName = recipient.recipientEmail.value,
                paymentDueDate = args.paymentDeadline.value.toString(),
                taxRate = 10.toString(),
                invoiceTaxExcludedAmount = args.invoiceAmount.value.toString(),
            )

            // issuerInvoiceテーブルに挿入
            DomainEventRepository().publish(
                tenantNameId,
                issuerInvoiceRegistered,
                domainEventContext.callUUID,
                handle,
            )

            ApplicationResult.Success
        }
    }
}
