import "react-toastify/dist/ReactToastify.css";

import { Button, Select } from "@mantine/core";
import React, { FC } from "react";
import { PdfDropZone } from "src/components/molcures/PdfDropZone";
import { useRecipients } from "src/hooks/recipient/useRecipient";
import {
  RecipientInvoiceUploadFormValues,
  useRecipientInvoiceUploadForm,
} from "src/hooks/useRecipientInvoiceUploadForm";
import { useSuppliers } from "src/hooks/recipient/useSuppliers";
import { useUploadInvoice } from "src/hooks/recipient/invoce/useUploadInvoice";
import { toast, ToastContainer } from "react-toastify";
import { useAuth } from "src/util/useAuth";

type RecipientInvoiceUploadFromProps = {
  onFileDrop: (file: File) => void;
};
export const RecipientInvoiceUploadForm: FC<
  RecipientInvoiceUploadFromProps
> = ({ onFileDrop }) => {
  const suppliers = useSuppliers();
  const invoiceOwner = useRecipients();
  const { getTenantNameId } = useAuth();
  const { values, getInputProps, onSubmit, setFieldValue, reset } =
    useRecipientInvoiceUploadForm();

  const handleOnDrop = (files: File[]) => {
    //  TODO(FE): 課題3
    // ファイルがドロップされた時の処理を実装
    onFileDrop(files[0] as File);
    toast.success("請求書をアップロードしました。", {
      position: "bottom-left",
    });
  };

  const handleSubmit = async (value: RecipientInvoiceUploadFormValues) => {
    //  TODO(FE): 課題3
    console.log(value);
    toast.success("請求書の登録に成功しました。", {
      position: "bottom-left",
    });
    reset();
  };

  return (
    <>
      <p>選択中のテナント:{getTenantNameId()}</p>
      <form onSubmit={onSubmit(handleSubmit)}>
        <p>取引先</p>
        <div style={{ marginBottom: "20px" }}>
          <Select
            name={"supplierUUID"}
            value={values.supplier}
            data={suppliers.map((it) => {
              return { value: it.tenantUUID, label: it.tenantNameId };
            })}
            {...getInputProps("supplierUUID")}
            />
          <p>所有者</p>
          <Select
            name={"invoiceOwner"}
            value={values.invoiceOwner}
            data={invoiceOwner.map((it) => {
              return { value: it.recipientUUID, label: it.fullName};
            })}
            {...getInputProps("invoiceOwner")}
            />
          </div>
        <PdfDropZone onDrop={handleOnDrop} />
        <br />
        <Button type={"submit"}>送信</Button>
      </form>
    </>
  );
};
