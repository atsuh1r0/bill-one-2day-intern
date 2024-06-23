import "@mantine/core/styles.css";
import React, { FC, useState } from "react";
import { RecipientHeader } from "src/components/templates/recipient/RecipientHeader";
import { GlobalFooter } from "src/components/templates/GlobalFooter";
import { Flex } from "@mantine/core";
import { RecipientInvoiceUploadForm } from "src/components/organisms/recipient/Invoice/RecipientInvoiceUploadForm";
import { RecipientInvoicePdfArea } from "src/components/organisms/RecipientInvoicePdfArea";

export const RecipientInvoiceUploadPage: FC = () => {
  const [urlString, setUrlString] = useState<string | undefined>(undefined);

  const dropFile = (file: File) => {
    //  TODO(FE): 課題3
    // ファイルがドロップされた時の処理を実装
    // 請求書をアップロードする処理を実装
    console.log("file dropped", file);
    setUrlString(URL.createObjectURL(file));
  };
  return (
    <>
      <RecipientHeader />
      <div style={{ height: "90%" }}>
        <div style={{ marginLeft: "3%" }}>
          <h2>請求書登録</h2>
        </div>
        <Flex justify={"space-evenly"}>
          <div style={{ width: "50%" }}>
            <RecipientInvoiceUploadForm onFileDrop={dropFile} />
          </div>
          <RecipientInvoicePdfArea
            invoiceResource={urlString}
            width={40}
            height={75}
          />
        </Flex>
      </div>
      <GlobalFooter />
    </>
  );
};
