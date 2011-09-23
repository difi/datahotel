//package no.difi.datahotel.util.csv;
//
//import static org.junit.Assert.assertArrayEquals;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Hashtable;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//public class CSVparserImplTest {
//
//	private File csvFile;
//	private char delimiter;
//	private File csvLarge;
//	private CSVParser parserImpl;
//
//	private String[] headerLine = {"__color__", "__group__", "__style__",
//			"ticket", "summary", "component", "status",
//			"resolution", "version", "type", "priority",
//			"owner", "modified", "_time", "_reporter;;;"};
//
//	private String[] lineOne = {"3","Group 5 - Backlog","","36","Legge til funksjonalitet for a skille mellom Linux og Windows",
//			"component1","new","","","enhancement","major","somebody",
//			"2011-06-25T13:02:55+0200","13:26:09+0200","ebe@…;;;"};
//
////	private String[] lineTwo = {"3","Group 4 - Sprint 3", "", "105", "Bean for editing of metadata",
////			"component1", "new", "", "", "task", "major", "dustng-ans@â€¦", 
////			"2011-07-04T15:02:42+0200", "15:02:42+0200", "dustNG-hgu@â€¦"};
//
//	private void initTest() throws Exception{
//		URL url = this.getClass().getResource("/ANSII_csv.csv");
//		csvFile = new File(url.getFile());
//		delimiter = ',';
//
//		URL url2 = this.getClass().getResource("/large.csv");
//		csvLarge = new File(url2.getFile());
//
//		parserImpl = CSVParserFactory.getCSVParserSmart(csvLarge);
//	}
//
//	@Before
//	public void before() throws Exception{
//		initTest();	
//	}
//
//	@After
//	public void after() throws Exception{
//		parserImpl.close();
//	}
//
//	//Tester getHeaders, men testen blir ikke kjort hvis den heter det.
//	@Test
//	public void getHeaders() throws Exception{
//		String[] headers = parserImpl.getHeaders();
//		//Test getHeaders
//		assertArrayEquals(headerLine, headers);
//	}
//
//	@Test
//	public void getTwoFirstLines()throws Exception{
//		List<String[]> result = new ArrayList<String[]>();
//		result = parserImpl.getTwoFirstLines();
//		//Test getTwoFirstLines
//		assertArrayEquals("getTwoFirstLines: headers", headerLine, result.get(0));
//		assertArrayEquals("getTwoFirstLines: headers", lineOne, result.get(1));
//	}
//
//
//	@Test
//	public void getDelimiter(){
//		char delimiterGet = ',';
//		//Test getDelimiter
//		assertEquals(delimiterGet, parserImpl.getDelimiter());
//	}
//
//	@Test
//	public void getNextLine()throws Exception{
//		Map<String, String> compare1 = new LinkedHashMap<String, String>();
//
//		compare1.put("__color__", "3");
//		compare1.put("__group__", "Group 5 - Backlog");
//		compare1.put("__style__", "");
//		compare1.put("ticket", "36");
//		compare1.put("summary", "Legge til funksjonalitet for a skille mellom Linux og Windows");
//		compare1.put("component", "component1");
//		compare1.put("status", "new");
//		compare1.put("resolution", "");
//		compare1.put("version", "");
//		compare1.put("type", "enhancement");
//		compare1.put("priority", "major");
//		compare1.put("owner", "somebody");
//		compare1.put("modified", "2011-06-25T13:02:55+0200");
//		compare1.put("_time", "13:26:09+0200");
//		compare1.put("_reporter;;;", "ebe@…;;;");
//
//		Map<String, String> compare2 = new LinkedHashMap<String, String>();
//
//		compare2.put("__color__", "3");
//		compare2.put("__group__", "Group 4 - Sprint 3");
//		compare2.put("__style__", "");
//		compare2.put("ticket", "105");
//		compare2.put("summary", "Bean for editing of metadata");
//		compare2.put("component", "component1");
//		compare2.put("status", "new");
//		compare2.put("resolution", "");
//		compare2.put("version", "");
//		compare2.put("type", "task");
//		compare2.put("priority", "major");
//		compare2.put("owner", "dustng-ans@…");
//		compare2.put("modified", "2011-07-04T15:02:42+0200");
//		compare2.put("_time", "15:02:42+0200");
//		compare2.put("_reporter;;;", "dustNG-hgu@…;;;");
//
//
//		Map<String, String> resultTable;
//		if(parserImpl.hasNext()) {
//			resultTable = parserImpl.getNextLine();
//			assertNotNull(resultTable);
//			assertEquals(compare1, resultTable);
//		}
//		if(parserImpl.hasNext()){
//			resultTable = parserImpl.getNextLine();
//			assertNotNull(resultTable);
//			assertEquals(compare2, resultTable);
//		}
//	}
//
//	@Test
//	public void testNextLine(){
//		String[] compare1 = {"Navn", "postnummer", "person", "ferjer" };
//
//		Hashtable<String,String> compare2 = new Hashtable<String, String>();
//		compare2.put("Navn", "per");
//		compare2.put("postnummer", "1234");
//		compare2.put("person", "ingen");	
//		compare2.put("ferjer", "nei");
//
//		Hashtable<String, String> compare3 = new Hashtable<String, String>();
//		compare3.put("Navn", "test");
//		compare3.put("postnummer", "23409i6");
//		compare3.put("person", "ja");	
//		compare3.put("ferjer", "Olsen");
//
//
//		CSVParser parser;
//		String[] firstLine;
//		Object[] array = null;
//		try {
//			// Checks if the first line is the actual first line from the file
//			parser = CSVParserFactory.getCSVParser(csvFile, delimiter);
//			firstLine = parser.getHeaders();
//			for (int i = 0; i < compare1.length; i++) {
//				assertEquals(firstLine[i], compare1[i]);					
//			}
//
//			// Gets the next line in the file, which should be the second line in the file
//			if(parser.hasNext())
//				array = parser.getNextLine().values().toArray();
//			assertNotNull(array);
//			for (int i = 0; i < compare2.size(); i++) {
//				assertTrue("output: " + array[i] + " compare2" + compare2.toString(),compare2.containsValue(array[i]));					
//			}
//			// Checks that getFirstLine still returns the first line in the file.
//			firstLine = parser.getHeaders();
//			for (int i = 0; i < compare1.length; i++) {
//				assertEquals(firstLine[i], compare1[i]);					
//			}
//			// Gets the next line again, this should be the third line in the file (not affected by getFirstLine).
//			if(parser.hasNext())
//				array = parser.getNextLine().values().toArray();
//			assertNotNull(array);
//			for (int i = 0; i < compare3.size(); i++) {
//				assertTrue("output: " + array[i], compare3.containsValue(array[i]));					
//			}
//			parser = new CSVParserImpl(csvFile, new CSVMetainfo(delimiter, null, true));
//			// Checks that getNxtLine now returns the second line of the file again.
//			if(parser.hasNext())
//				array = parser.getNextLine().values().toArray();
//			assertNotNull(array);
//			for (int i = 0; i < compare2.size(); i++) {
//				assertTrue(compare2.containsValue(array[i]));					
//			}
//
//			URL url = this.getClass().getResource("/ANSII_csv_semicolon.csv");
//			csvFile = new File(url.getFile());
//			delimiter = ';';
//			parser = new CSVParserImpl(csvFile, new CSVMetainfo(delimiter, null, true));
//			// Checks that getNxtLine now returns the second line of the file again.
//			if(parser.hasNext())
//				array = parser.getNextLine().values().toArray();
//			assertNotNull(array);
//			for (int i = 0; i < array.length; i++) {
//				assertTrue("Array contains: " + array.toString(), compare2.containsValue(array[i]));					
//			}
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			fail("Uanble to load CSV file");
//		} catch (IOException e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		}		
//	}
//}
