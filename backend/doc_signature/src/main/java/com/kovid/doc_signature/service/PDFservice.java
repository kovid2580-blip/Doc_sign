package com.kovid.doc_signature.service;


import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;


@Service
public class PDFservice {


    public String addSignature(

            String inputPath,

            String signerName,

            float x,

            float y,

            int pageNumber

    ) throws IOException {


        PDDocument document =
                Loader.loadPDF(new File(inputPath));

        int pageIndex = pageNumber - 1;

        PDPage page =
                document.getPage(pageIndex);



        PDPageContentStream stream =
                new PDPageContentStream(
                        document,
                        page,
                        PDPageContentStream.AppendMode.APPEND,
                        true
                );


        stream.beginText();

       stream.setFont(
        new PDType1Font(
                Standard14Fonts.FontName.HELVETICA_BOLD
        ),
        14
);


        stream.newLineAtOffset(
                x,
                y
        );


        stream.showText(
                "Signed by: " + signerName
        );


        stream.endText();

        stream.close();



        String output = signedOutputPath(inputPath);


        document.save(output);

        document.close();


        return output;

    }

    private String signedOutputPath(String inputPath) {

        if (inputPath.toLowerCase(Locale.ROOT).endsWith(".pdf")) {

            return inputPath.substring(0, inputPath.length() - 4) + "_signed.pdf";

        }

        return inputPath + "_signed.pdf";

    }

}
