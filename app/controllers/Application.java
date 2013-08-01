package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Element;

import java.io.ByteArrayOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.Random;

import java.util.*;

public class Application extends Controller {

    //印刷
    public static Result mathprint(String param) {

        String titleStr = "";
        List<String> quetionList = new ArrayList<String>();

        if("Addition2".equals(param)){
            titleStr = "足し算(1桁,繰り上がりあり)";
            quetionList = getQuetionList();
        } else if("Addition1".equals(param)){
            titleStr = "足し算(1桁,繰り上がりなし)";
            quetionList = getQuetionList2();
        } else if("Subtraction1".equals(param)){
            titleStr = "引き算(1桁)";
            quetionList = getQuetionList3();
        } else if("Multiplication1".equals(param)){
            titleStr = "かけ算";
            quetionList = getQuetionList4();
        } else if("Division1".equals(param)){
            titleStr = "わり算";
            quetionList = getQuetionList5();
        } else {
            return internalServerError();
        }

        ByteArrayOutputStream result =  new ByteArrayOutputStream();
        try {
            Font fnt1 = new Font(BaseFont.createFont("lib/ipag.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 15);
            Font fnt2 = new Font(BaseFont.createFont("lib/ipag.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 15);

            Document document = new Document(PageSize.A4,20,20,20,20);
            PdfWriter writer = PdfWriter.getInstance(document,result);
            document.open();

            //pdfの付加情報
            document.addTitle("算数プリント");
            document.addSubject("math printer");
            document.addAuthor("math printer");
            document.addCreator("math printer");
            document.addKeywords("");

            //空行
            Paragraph breakparag = new Paragraph(" ");

            //タイトル
            {
              PdfPTable tblPdfPTable = new PdfPTable(2);
              tblPdfPTable.setWidthPercentage(100);
              tblPdfPTable.setWidths(new float[]{ 50.0f,50.0f});

              tblPdfPTable.addCell(createCellNonBorder(tblPdfPTable, titleStr, fnt1, BaseColor.WHITE, Element.ALIGN_LEFT,Element.ALIGN_MIDDLE, 3.0f));
              String nameSpace = "名前(                     )";
              tblPdfPTable.addCell(createCellNonBorder(tblPdfPTable,nameSpace, fnt1, BaseColor.WHITE, Element.ALIGN_RIGHT,Element.ALIGN_MIDDLE, 3.0f));
              document.add(tblPdfPTable);
            }

            document.add(breakparag); //改行
            document.add(breakparag); //改行
            document.add(breakparag); //改行

            for (int i = 0; i <= quetionList.size() -1 ; i++) {
              PdfPTable tblPdfPTable = new PdfPTable(2);
              tblPdfPTable.setWidthPercentage(100);
              tblPdfPTable.setWidths(new float[]{ 50.0f,50.0f});
              String quetionA = String.format("%5s %s", "("+(i+1)+")", quetionList.get(i));
              String quetionB = String.format("%5s %s", "("+(i+2)+")", quetionList.get(i + 1));
              tblPdfPTable.addCell(createCellNonBorder(tblPdfPTable,quetionA, fnt2, BaseColor.WHITE, Element.ALIGN_LEFT,Element.ALIGN_MIDDLE, 20.0f));
              tblPdfPTable.addCell(createCellNonBorder(tblPdfPTable,quetionB, fnt2, BaseColor.WHITE, Element.ALIGN_LEFT,Element.ALIGN_MIDDLE, 20.0f));
              document.add(tblPdfPTable);
              i++;
            }

            document.close();
        } catch (IOException e){
            Logger.error("[pdf make Error:IOException]",e);
        } catch (DocumentException e) {
            Logger.error("[pdf make Error:DocumentException]",e);
        }

        String FileName = "算数プリント.pdf";

        try {
            String dFileName = URLEncoder.encode(FileName, "UTF-8");

            response().setHeader("Cache-Control", "public");
            response().setHeader("Content-Description", "File Transfer");
            response().setHeader("Content-type", "application/force-download;charset=UTF-8");
            response().setHeader("Content-Disposition", "attachment; filename="+ dFileName);
            response().setHeader("Content-Type",  "application/pdf");
            response().setHeader("Content-Transfer-Encoding", "binary");

        } catch (UnsupportedEncodingException e) {
            Logger.error("[pdf make Error:UnsupportedEncodingException]",e);
        }
        return ok(result.toByteArray()).as("application/pdf");

    }

    //セル生成
    private static PdfPCell createCellNonBorder(PdfPTable tblPdfPTable, String str ,Font fnt, BaseColor color, int horizontalaliment, int VerticalAlignment, float padding){
        PdfPCell cellPdfPCell = new PdfPCell(new Phrase(str,fnt));
        cellPdfPCell.setBackgroundColor(color);
        cellPdfPCell.setHorizontalAlignment(horizontalaliment);
        cellPdfPCell.setVerticalAlignment(VerticalAlignment);
        cellPdfPCell.setPadding(padding);
        cellPdfPCell.setBorder(0);
        return cellPdfPCell;
    }

    //足し算の問題（繰り上がり含む)
    private static List<String> getQuetionList()
    {
       List<String> quetionList = new ArrayList<String>();
       while( quetionList.size() < 20){
            String quetion = "";

            Random rnd = new Random();
            int leftInt = rnd.nextInt(9) + 1;
            int rightInt = rnd.nextInt(9) + 1;
            quetion = String.format("%d + %d = ", leftInt, rightInt);

            if(!quetionList.contains(quetion)){
                quetionList.add(quetion);
            }
        }
        return quetionList;
    }

    //足し算の問題（繰り上がりなし）
    private static List<String> getQuetionList2()
    {
       List<String> quetionList = new ArrayList<String>();
       while( quetionList.size() < 20){
            String quetion = "";

            Random rnd = new Random();
            int leftInt = rnd.nextInt(9) + 1;
            int rightInt = rnd.nextInt(9) + 1;
            if(leftInt + rightInt < 10){
                quetion = String.format("%d + %d = ", leftInt, rightInt);

                if(!quetionList.contains(quetion)){
                    quetionList.add(quetion);
                }
            }
        }
        return quetionList;
    }

    //引き算
    private static List<String> getQuetionList3()
    {
      List<String> quetionList = new ArrayList<String>();
      while( quetionList.size() < 20){
        String quetion = "";

        Random rnd = new Random();
        int leftInt = rnd.nextInt(9) + 1;
        int rightInt = rnd.nextInt(9) + 1;
        if(leftInt - rightInt >= 0){
          quetion = String.format("%d - %d = ", leftInt, rightInt);

          if(!quetionList.contains(quetion)){
            quetionList.add(quetion);
          }
        }
      }
      return quetionList;
    }

    //かけ算
    private static List<String> getQuetionList4()
    {
       List<String> quetionList = new ArrayList<String>();
       while( quetionList.size() < 20){
            String quetion = "";

            Random rnd = new Random();
            int leftInt = rnd.nextInt(9) + 1;
            int rightInt = rnd.nextInt(9) + 1;
            quetion = String.format("%d × %d = ", leftInt, rightInt);

            if(!quetionList.contains(quetion)){
                quetionList.add(quetion);
            }
        }
        return quetionList;
    }

    //割り算
    private static List<String> getQuetionList5()
    {
       List<String> quetionList = new ArrayList<String>();
       while( quetionList.size() < 20){
            String quetion = "";

            Random rnd = new Random();
            int rightInt = rnd.nextInt(5) + 2;
            int leftInt = rightInt * (rnd.nextInt(5) + 1);
            if(leftInt > rightInt && ( leftInt % rightInt == 0  )){
              quetion = String.format("%d ÷ %d = ", leftInt, rightInt);

              if(!quetionList.contains(quetion)){
                quetionList.add(quetion);
              }
            }
        }
        return quetionList;
    }

}
