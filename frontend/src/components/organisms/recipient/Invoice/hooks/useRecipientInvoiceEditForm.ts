import { useForm } from "@mantine/form";
import { Invoice } from "src/domain/models/invoice";

export type RecipientInvoiceEditFormValues = {
  invoiceUUID: string | undefined;
  invoiceAmount: number | undefined;
  supplierUUID: string | undefined;
  paymentDeadline: Date | undefined;
};

export const useRecipientInvoiceEditForm = (
  invoice: Invoice,
  submit: (invoice: RecipientInvoiceEditFormValues) => Promise<void>,
) => {
  const { values, getInputProps, onSubmit } =
    useForm<RecipientInvoiceEditFormValues>(
      {
        initialValues: {
          invoiceUUID: invoice.invoiceUUID,
          invoiceAmount: invoice.amount,
          supplierUUID: invoice.supplierUUID,
          paymentDeadline: invoice.paymentDeadline,
        },
      },
    );

  const handleSubmit = () => {
    submit({
      invoiceUUID: invoice.invoiceUUID,
      invoiceAmount: values.invoiceAmount,
      supplierUUID: values.supplierUUID,
      paymentDeadline: values.paymentDeadline,
    });
  };

  return {
    values,
    submit: onSubmit(handleSubmit),
    getInputProps,
  };
};
