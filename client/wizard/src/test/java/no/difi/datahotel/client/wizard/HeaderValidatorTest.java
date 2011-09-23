package no.difi.datahotel.client.wizard;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

import no.difi.datahotel.logic.model.FieldDTO;

/**
 *
 * @author oho
 */
public class HeaderValidatorTest {

    private HeaderValidator validator;
        
    @BeforeClass
    public static void setUpClass() throws Exception {
     
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        validator = new HeaderValidator();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void checkDuplicatHeadersIsFalseTest()
    {
        List<FieldDTO> fields = new ArrayList<FieldDTO>();
        FieldDTO field1=new FieldDTO();
        field1.setField("something");
        FieldDTO field2=new FieldDTO();
        field2.setField("something other");

        fields.add(field1);
        fields.add(field2);

        assertTrue(validator.checkforNoneUniquHeaders(fields));

    }

    @Test
    public void checkDuplicatHeadersIsTrueTest() {
        List<FieldDTO> fields = new ArrayList<FieldDTO>();
        FieldDTO field1=new FieldDTO();
        field1.setField("something");
        FieldDTO field2=new FieldDTO();
        field2.setField("something");

        fields.add(field1);
        fields.add(field2);

        assertFalse(validator.checkforNoneUniquHeaders(fields));
    }

    @Test
    public void checkForIllegalChars() {
        String fieldWithWhiteSpace="a field";
        String fieldWithNordicChars="ækææ";

        assertTrue(validator.checkFieldsForIllegalChars(fieldWithWhiteSpace));
        assertTrue(validator.checkFieldsForIllegalChars(fieldWithNordicChars));

    }

}