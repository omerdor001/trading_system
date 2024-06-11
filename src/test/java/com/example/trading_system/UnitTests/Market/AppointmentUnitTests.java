/*
package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.users.Registered;
import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.service.UserServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

//TODO FIX ME
public class AppointmentUnitTests {
    //TODO update tests to match TradingSystem function flow
    UserServiceImp userServiceImp;
    UserFacadeImp userFacadeImp;
    Registered registered;
    Registered registered2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userFacadeImp = UserFacadeImp.getInstance();
        userServiceImp = UserServiceImp.getInstance();
        registered = mock(Registered.class);
        registered2 = mock(Registered.class);
    }

    @Test
    void suggestManager_Success() throws IllegalAccessException, NoSuchFieldException {

        Field registeredField = UserFacadeImp.class.getDeclaredField("registered");
        registeredField.setAccessible(true);
        Map<String, Registered> registeredUsers = new HashMap<>();
        registeredUsers.put("appointUser", registered);
        registeredUsers.put("newManagerUser", registered2);
        registeredField.set(userFacadeImp, registeredUsers);

        when(registered.isOwner(anyString())).thenReturn(true);
        when(registered.getLogged()).thenReturn(true);
        when(registered2.isManager(anyString())).thenReturn(false);
        when(registered2.isOwner(anyString())).thenReturn(false);
        //  doNothing().when(registered2).addWaitingAppoint_Manager(anyString(),anyBoolean(),anyBoolean(),anyBoolean(),anyBoolean());
        //ResponseEntity<String> response = userServiceImp.suggestManage("appointUser", "newManagerUser", "store1",true, true, true, true);
        //assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void suggestManager_AppointDoesntExist() throws IllegalAccessException, NoSuchFieldException {

        Field registeredField = UserFacadeImp.class.getDeclaredField("registered");
        registeredField.setAccessible(true);
        Map<String, Registered> registeredUsers = new HashMap<>();
        registeredUsers.put("appointUser", registered);
        registeredUsers.put("newManagerUser", registered2);
        registeredField.set(userFacadeImp, registeredUsers);

        //ResponseEntity<String> response = userServiceImp.suggestManage("notexistuser", "newManagerUser", "store1",true, true, true, true);
        //assertEquals(response.getBody(),"No user called notexistuser exist");
        //assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void suggestManager_newManagerDoesntExist() throws IllegalAccessException, NoSuchFieldException {

        Field registeredField = UserFacadeImp.class.getDeclaredField("registered");
        registeredField.setAccessible(true);
        Map<String, Registered> registeredUsers = new HashMap<>();
        registeredUsers.put("appointUser", registered);
        registeredUsers.put("newManagerUser", registered2);
        registeredField.set(userFacadeImp, registeredUsers);

        //ResponseEntity<String> response = userServiceImp.suggestManage("appointUser", "notExist", "store1",true, true, true, true);
        //assertEquals(response.getBody(),"No user called notExist exist");
        //assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void suggestManager_appointUserIsNotOwner() throws IllegalAccessException, NoSuchFieldException {

        Field registeredField = UserFacadeImp.class.getDeclaredField("registered");
        registeredField.setAccessible(true);
        Map<String, Registered> registeredUsers = new HashMap<>();
        registeredUsers.put("appointUser", registered);
        registeredUsers.put("newManagerUser", registered2);
        registeredField.set(userFacadeImp, registeredUsers);

        when(registered.isOwner(anyString())).thenReturn(false);
        //ResponseEntity<String> response = userServiceImp.suggestManage("appointUser", "newManagerUser", "store1",true, true, true, true);
        //assertEquals(response.getBody(),"Appoint user must be Owner");
        //assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void suggestManager_appointUserIsNotLogged() throws IllegalAccessException, NoSuchFieldException {

        Field registeredField = UserFacadeImp.class.getDeclaredField("registered");
        registeredField.setAccessible(true);
        Map<String, Registered> registeredUsers = new HashMap<>();
        registeredUsers.put("appointUser", registered);
        registeredUsers.put("newManagerUser", registered2);
        registeredField.set(userFacadeImp, registeredUsers);

        when(registered.isOwner(anyString())).thenReturn(true);
        when(registered.getLogged()).thenReturn(false);

        //ResponseEntity<String> response = userServiceImp.suggestManage("appointUser", "newManagerUser", "store1",true, true, true, true);
        //assertEquals(response.getBody(),"Appoint user is not logged");
        //assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }


    @Test
    void suggestManager_newManagerIsAlreadyManager() throws IllegalAccessException, NoSuchFieldException {

        Field registeredField = UserFacadeImp.class.getDeclaredField("registered");
        registeredField.setAccessible(true);
        Map<String, Registered> registeredUsers = new HashMap<>();
        registeredUsers.put("appointUser", registered);
        registeredUsers.put("newManagerUser", registered2);
        registeredField.set(userFacadeImp, registeredUsers);

        when(registered.isOwner(anyString())).thenReturn(true);
        when(registered.getLogged()).thenReturn(true);
        when(registered2.isManager(anyString())).thenReturn(true);

        //ResponseEntity<String> response = userServiceImp.suggestManage("appointUser", "newManagerUser", "store1",true, true, true, true);
        //assertEquals(response.getBody(),"User already Manager of this store");
        //assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void suggestManager_newManagerIsAlreadyOwner() throws IllegalAccessException, NoSuchFieldException {
        Field registeredField = UserFacadeImp.class.getDeclaredField("registered");
        registeredField.setAccessible(true);
        Map<String, Registered> registeredUsers = new HashMap<>();
        registeredUsers.put("appointUser", registered);
        registeredUsers.put("newManagerUser", registered2);
        registeredField.set(userFacadeImp, registeredUsers);

        when(registered.isOwner(anyString())).thenReturn(true);
        when(registered.getLogged()).thenReturn(true);
        when(registered2.isManager(anyString())).thenReturn(false);
        when(registered2.isOwner(anyString())).thenReturn(true);

        //ResponseEntity<String> response = userServiceImp.suggestManage("appointUser", "newManagerUser", "store1",true, true, true, true);
        //assertEquals(response.getBody(),"User cannot be owner of this store");
        //assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void approveManager_Success() throws IllegalAccessException, NoSuchFieldException {

        List<Boolean> perm = Arrays.asList(true, true, true, true);
        Field registeredField = UserFacadeImp.class.getDeclaredField("registered");
        registeredField.setAccessible(true);
        Map<String, Registered> registeredUsers = new HashMap<>();
        registeredUsers.put("appointUser", registered);
        registeredUsers.put("newManagerUser", registered2);
        registeredField.set(userFacadeImp, registeredUsers);

        when(registered.isOwner(anyString())).thenReturn(true);
        when(registered.getLogged()).thenReturn(true);
        when(registered2.isManager(anyString())).thenReturn(false);
        when(registered2.isOwner(anyString())).thenReturn(false);
        when(registered2.removeWaitingAppoint_Manager(anyString())).thenReturn(perm);
        //ResponseEntity<String> response = userServiceImp.approveManage("newManagerUser", "store1","appointUser");
        //assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void approveManager_AppointDoesntExist() throws IllegalAccessException, NoSuchFieldException {

        Field registeredField = UserFacadeImp.class.getDeclaredField("registered");
        registeredField.setAccessible(true);
        Map<String, Registered> registeredUsers = new HashMap<>();
        registeredUsers.put("appointUser", registered);
        registeredUsers.put("newManagerUser", registered2);
        registeredField.set(userFacadeImp, registeredUsers);

        //ResponseEntity<String> response = userServiceImp.approveManage("newManagerUser", "store1", "notexistuser");
        //assertEquals(response.getBody(),"No user called notexistuser exist");
        //assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void approveManager_newManagerDoesntExist() throws IllegalAccessException, NoSuchFieldException {

        Field registeredField = UserFacadeImp.class.getDeclaredField("registered");
        registeredField.setAccessible(true);
        Map<String, Registered> registeredUsers = new HashMap<>();
        registeredUsers.put("appointUser", registered);
        registeredUsers.put("newManagerUser", registered2);
        registeredField.set(userFacadeImp, registeredUsers);

        //ResponseEntity<String> response = userServiceImp.approveManage("notExist", "store1", "appointUser");
        //assertEquals(response.getBody(),"No user called notExist exist");
        //assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void approveManager_appointUserIsNotOwner() throws IllegalAccessException, NoSuchFieldException {

        Field registeredField = UserFacadeImp.class.getDeclaredField("registered");
        registeredField.setAccessible(true);
        Map<String, Registered> registeredUsers = new HashMap<>();
        registeredUsers.put("appointUser", registered);
        registeredUsers.put("newManagerUser", registered2);
        registeredField.set(userFacadeImp, registeredUsers);

        when(registered.isOwner(anyString())).thenReturn(false);

        //ResponseEntity<String> response = userServiceImp.approveManage("newManagerUser", "store1", "appointUser");
        //assertEquals(response.getBody(),"Appoint user must be Owner");
        //assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void approveManager_newManagerIsAlreadyManager() throws IllegalAccessException, NoSuchFieldException {

        Field registeredField = UserFacadeImp.class.getDeclaredField("registered");
        registeredField.setAccessible(true);
        Map<String, Registered> registeredUsers = new HashMap<>();
        registeredUsers.put("appointUser", registered);
        registeredUsers.put("newManagerUser", registered2);
        registeredField.set(userFacadeImp, registeredUsers);

        when(registered.isOwner(anyString())).thenReturn(true);
        when(registered2.isManager(anyString())).thenReturn(true);

        //ResponseEntity<String> response = userServiceImp.approveManage("newManagerUser", "store1", "appointUser");
        //assertEquals(response.getBody(),"User already Manager of this store");
        //assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void approveManager_newManagerIsAlreadyOwner() throws IllegalAccessException, NoSuchFieldException {

        Field registeredField = UserFacadeImp.class.getDeclaredField("registered");
        registeredField.setAccessible(true);
        Map<String, Registered> registeredUsers = new HashMap<>();
        registeredUsers.put("appointUser", registered);
        registeredUsers.put("newManagerUser", registered2);
        registeredField.set(userFacadeImp, registeredUsers);

        when(registered.isOwner(anyString())).thenReturn(true);
        when(registered.getLogged()).thenReturn(true);
        when(registered2.isManager(anyString())).thenReturn(false);
        when(registered2.isOwner(anyString())).thenReturn(true);

        //ResponseEntity<String> response = userServiceImp.approveManage("newManagerUser", "store1", "appointUser");
        //assertEquals(response.getBody(),"User cannot be owner of this store");
        //assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

}
*/
