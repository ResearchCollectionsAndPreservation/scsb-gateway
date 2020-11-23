package org.recap.controller;

import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.util.PropertyUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MessageRestControllerUT extends BaseTestCase {

    @Mock
    MessageRestController MessageRestController;

    @Mock
    PropertyUtil propertyUtil;

    @Test
    public void getValue() throws Exception {
        JSONObject json=new JSONObject();
        Mockito.when(propertyUtil.getPropertyByInstitution(Mockito.anyString())).thenReturn(json);
        ReflectionTestUtils.setField(MessageRestController,"propertyUtil",propertyUtil);
        Mockito.when(MessageRestController.getValue("PUL")).thenCallRealMethod();
        Map<String, Object> response= MessageRestController.getValue("PUL");
        Mockito.when(MessageRestController.getValue()).thenCallRealMethod();
        String response1= MessageRestController.getValue();
        assertEquals("response",response1);
    }
}
