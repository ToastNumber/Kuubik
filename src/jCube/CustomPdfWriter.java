package jCube;

import java.io.FileOutputStream;
import java.util.Date;

import javax.swing.JOptionPane;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author Kelsey McKenna
 */
public class CustomPdfWriter {
	/**
	 * This stores a font for the headings of the table stored when saving
	 * statistics.
	 */
	private static Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);

	/**
	 * Creates the statistics pdf
	 * 
	 * @param filePath
	 *            the path to which the pdf file should be saved
	 * @param timeData
	 *            the times from which the statistics are calculated
	 */
	public static void createStatisticsPdf(String filePath, String[] timeData) {
		Document document = new Document();

		try {
			PdfWriter.getInstance(document, new FileOutputStream(filePath));
			document.open();
			addMetaData(document);
			addTimesPage(document, timeData);
			document.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Writing to File", "Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			try {
				document.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Adds meta data to the pdf document
	 * 
	 * @param document
	 *            the document to which the meta data is to be added
	 */
	private static void addMetaData(Document document) {
		document.addTitle("Rubik's Cube Statistics - " + new Date());
		document.addAuthor(System.getProperty("user.name"));
		document.addKeywords("Rubik, Cube, Statistics");
	}

	/**
	 * Adds the specific contents to the document
	 * 
	 * @param document
	 *            the document to which the contents are added
	 * @param timeData
	 *            the data from which the statistics are calculated
	 * @throws DocumentException
	 *             if there was an error writing to the document
	 */
	private static void addTimesPage(Document document, String[] timeData) throws DocumentException {
		Paragraph timesChapter = new Paragraph();
		timesChapter.add(new Paragraph(" ")); // Empty line/separator
		timesChapter.add(getStatisticsTable(timeData));
		document.add(timesChapter);

		document.newPage();
	}

	/**
	 * @param timeData
	 *            the times from which statistics are calculated
	 * @return a formatted table containing the statistics
	 * @throws DocumentException
	 *             if there was an error writing to the document
	 */
	private static PdfPTable getStatisticsTable(String[] timeData) throws DocumentException {
		PdfPTable table = new PdfPTable(3);
		int[] widths = { 20, 10, 40 };
		table.setWidthPercentage(100);
		table.setWidths(widths);

		PdfPCell c1 = new PdfPCell(new Phrase("Average Type", tableHeaderFont));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		c1.setVerticalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Average", tableHeaderFont));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		c1.setVerticalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Times", tableHeaderFont));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		c1.setVerticalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		table.setHeaderRows(0);

		if (timeData != null) {
			for (int i = 0; i < timeData.length; ++i) {
				c1 = new PdfPCell(new Phrase(timeData[i]));
				c1.setPaddingTop(13F);
				c1.setPaddingBottom(20F);
				table.addCell(c1);
			}
		} else {
			table.addCell("No data to display");
			table.addCell("No data to display");
			table.addCell("No data to display");
		}

		return table;
	}

}
