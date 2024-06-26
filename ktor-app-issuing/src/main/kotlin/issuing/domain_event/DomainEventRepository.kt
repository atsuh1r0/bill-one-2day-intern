package issuing.domain_event

import com.sansan.billone.lib.CallUUID
import issuing.domain.tenant.TenantNameId
import org.jdbi.v3.core.Handle
import org.postgresql.util.PGobject
import java.util.*

class DomainEventRepository {
  fun publish(
    tenantNameId: TenantNameId,
    domainEvent: DomainEvent,
    callUUID: CallUUID,
    handle: Handle,
  ) {
    publish(tenantNameId, listOf(domainEvent), callUUID, handle)
  }

  fun publish(
    tenantNameId: TenantNameId,
    domainEvents: List<DomainEvent>,
    callUUID: CallUUID,
    handle: Handle,
  ) {
    val sql =
      """
            INSERT INTO domain_event (
                domain_event_uuid,
                call_uuid,
                domain_event_name,
                tenant_name_id,
                message,
                deployed,
                created_at
            ) VALUES (
                :domainEventUUID,
                :callUUID,
                :domainEventName,
                :tenantNameId,
                :message,
                false,
                now()
            )
            """.trimIndent()

    val batch = handle.prepareBatch(sql)
    domainEvents.forEach { domainEvent ->
      batch
        .bind("tenantNameId", tenantNameId.value)
        .bind("domainEventUUID", UUID.randomUUID())
        .bind("callUUID", callUUID.value)
        .bind("domainEventName", domainEvent.javaClass.kotlin.qualifiedName)
        .bind(
          "message",
          PGobject().apply {
            type = "json"
            value = domainEvent.toJSON()
          },
        )
        .add()
    }

    batch.execute()
  }

  fun getByCallUUID(
    callUUID: CallUUID,
    handle: Handle,
  ): List<DomainEventRow> {
    val selectPaymentSQL =
      """
            SELECT
                domain_event_uuid as domainEventUUID,
                call_uuid as callUUID,
                domain_event_name as domainEventName,
                message,
                deployed
            FROM domain_event
            WHERE call_uuid = :callUUID
            ORDER BY created_at
            """.trimIndent()

    return handle.createQuery(selectPaymentSQL)
      .bind("callUUID", callUUID.value)
      .mapTo(DomainEventRow::class.java)
      .list()
  }

  fun markAsDeployed(
    callUUID: CallUUID,
    handle: Handle,
  ) {
    val sql =
      """
            UPDATE 
                domain_event
            SET 
                deployed = true
            WHERE 
                call_uuid = :callUUID
            """.trimIndent()

    handle.createUpdate(sql)
      .bind("callUUID", callUUID.value)
      .execute()
  }
}
