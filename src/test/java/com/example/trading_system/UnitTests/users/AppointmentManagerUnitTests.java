package com.example.trading_system.UnitTests.users;

import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;


class AppointmentManagerUnitTests {
    private UserFacade userFacade;
    private String username1;
    private String username2;
    private String username3;

    @BeforeEach
    public void setUp() {
        userFacade= UserFacadeImp.getInstance();
        username1="testuser1";
        username2="testuser2";
        username3="testuser3";
        try{
            userFacade.register(username1,username1,LocalDate.now());
            userFacade.register(username2,username2,LocalDate.now());
            userFacade.register(username3,username3,LocalDate.now());
            userFacade.enter(0);
            userFacade.enter(1);
            userFacade.enter(2);
            userFacade.login("v0",username1,username1);
            userFacade.login("v1",username2,username2);
            userFacade.login("v2",username3,username3);
            userFacade.createStore("r"+username1,"Adidas","");
            userFacade.createStore("r"+username2,"Nike","");
        }
        catch (Exception _){}
    }

    @AfterEach
    public void tearDown(){
        userFacade.logout(0,"r"+username1);
        userFacade.logout(1,"r"+username2);
        userFacade.logout(2,"r"+username3);
        try{
            userFacade.exit("v0");
            userFacade.exit("v1");
            userFacade.exit("v2");
            userFacade.deleteInstance();
        }
        catch (Exception _){}
    }

    //SuggestManage

    @Test
    void suggestManager_Success() {
        int sizeB=userFacade.getUser("r"+username2).getManagerToApprove().size();
        assertDoesNotThrow(() -> userFacade.suggestManage("r"+username1,"r"+username2,"Adidas",true,false,true,false), "suggestManage should not throw any exceptions");
        int sizeA=userFacade.getUser("r"+username2).getManagerToApprove().size();
        assertEquals(sizeB,sizeA-1);
    }

    @Test
    void suggestManager_StoreNotExist() {
        int sizeB=userFacade.getUser("r"+username2).getManagerToApprove().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->  userFacade.suggestManage("r"+username1,"r"+username2,"Adidas1",true,false,true,false));
        assertEquals("No store called Adidas1 exist", exception.getMessage());
        int sizeA=userFacade.getUser("r"+username2).getManagerToApprove().size();
        assertEquals(sizeB,sizeA);
    }

    @Test
    void suggestManager_UserToOwnerExist() {
        int sizeB=userFacade.getUser("r"+username2).getManagerToApprove().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->  userFacade.suggestManage("r"+username1,"r"+"username2","Adidas",true,false,true,false));
        assertEquals("No user called rusername2 exist", exception.getMessage());
        int sizeA=userFacade.getUser("r"+username2).getManagerToApprove().size();
        assertEquals(sizeB,sizeA);
    }

    @Test
    void suggestManager_AppointIsSuspended() {
        int sizeB=userFacade.getUser("r"+username3).getManagerToApprove().size();
        userFacade.suspendUser("r"+username1,"r"+username2, LocalDateTime.of(2025,1,1,1,1));
        RuntimeException exception = assertThrows(RuntimeException.class, () ->  userFacade.suggestManage("r"+username2,"r"+username3,"Nike",true,false,true,false));
        assertEquals("User is suspended from the system", exception.getMessage());
        userFacade.endSuspendUser("r"+username1,"r"+username2);
        int sizeA=userFacade.getUser("r"+username3).getManagerToApprove().size();
        assertEquals(sizeB,sizeA);
    }

    @Test
    void suggestManager_UserAppointNotOwner() {
        int sizeB=userFacade.getUser("r"+username3).getManagerToApprove().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () ->  userFacade.suggestManage("r"+username2,"r"+username3,"Adidas",true,false,true,false));
        assertEquals("Appoint user must be Owner", exception.getMessage());
        int sizeA=userFacade.getUser("r"+username3).getManagerToApprove().size();
        assertEquals(sizeB,sizeA);
    }

    @Test
    void suggestManager_UserAppointNotLogged() {
        int sizeB=userFacade.getUser("r"+username2).getManagerToApprove().size();
        userFacade.logout(0,"r"+username1);
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () ->  userFacade.suggestManage("r"+username1,"r"+username2,"Adidas",true,false,true,false));
        assertEquals("Appoint user is not logged", exception.getMessage());
        userFacade.login("v0",username1,username1);
        int sizeA=userFacade.getUser("r"+username2).getManagerToApprove().size();
        assertEquals(sizeB,sizeA);
    }

    @Test
    void suggestManager_AlreadyManager() {
        int sizeB=userFacade.getUser("r"+username3).getManagerToApprove().size();
        userFacade.getUser("r"+username3).addManagerRole("r"+username2,"Nike");    //For tests only!
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () ->  userFacade.suggestManage("r"+username2,"r"+username3,"Nike",true,false,true,false));
        assertEquals("User already Manager of this store", exception.getMessage());
        userFacade.getUser("r"+username2).removeManagerRole("Adidas");     //For tests only!
        int sizeA=userFacade.getUser("r"+username3).getManagerToApprove().size();
        assertEquals(sizeB,sizeA);
    }

    @Test
    void suggestManager_isOwner() {
        int sizeB=userFacade.getUser("r"+username2).getManagerToApprove().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () ->  userFacade.suggestManage("r"+username1,"r"+username1,"Adidas",true,false,true,false));
        assertEquals("User cannot be owner of this store", exception.getMessage());
        int sizeA=userFacade.getUser("r"+username2).getManagerToApprove().size();
        assertEquals(sizeB,sizeA);
    }

    //ApproveManager

    @Test
    void approveManager_Success() {
        try {
            userFacade.suggestManage("r"+username1,"r"+username2,"Adidas",true,false,true,false);
        }
        catch (Exception _){}
        int sizeB=userFacade.getUser("r"+username2).getManagerToApprove().size();
        assertDoesNotThrow(() -> userFacade.approveManage("r"+username2,"Adidas","r"+username1), "approveManage should not throw any exceptions");
        int sizeA=userFacade.getUser("r"+username2).getManagerToApprove().size();
        assertEquals(sizeB,sizeA+1);
    }

    @Test
    void approveManager_StoreNotExist() {
        try {
            userFacade.suggestManage("r"+username1,"r"+username2,"Adidas",true,false,true,false);
        }
        catch (Exception _){}
        int sizeB=userFacade.getUser("r"+username2).getManagerToApprove().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->  userFacade.approveManage("r"+username2,"Adidas1","r"+username1));
        assertEquals("No store called Adidas1 exist", exception.getMessage());
        int sizeA=userFacade.getUser("r"+username2).getManagerToApprove().size();
        assertEquals(sizeB,sizeA);
    }

    @Test
    void approveManager_UserToOwnerExist() {
        try {
            userFacade.suggestManage("r"+username1,"r"+username2,"Adidas",true,false,true,false);
        }
        catch (Exception _){}
        int sizeB=userFacade.getUser("r"+username2).getManagerToApprove().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->  userFacade.approveManage("r"+"username2","Adidas","r"+username1));
        assertEquals("No user called rusername2 exist", exception.getMessage());
        int sizeA=userFacade.getUser("r"+username2).getManagerToApprove().size();
        assertEquals(sizeB,sizeA);
    }

    @Test
    void approveManager_newManagerIsSuspended() {
        try {
            userFacade.suggestManage("r"+username3,"r"+username2,"Nike",true,false,true,false);
        }
        catch (Exception _){}
        int sizeB=userFacade.getUser("r"+username3).getManagerToApprove().size();
        userFacade.suspendUser("r"+username1,"r"+username3, LocalDateTime.of(2025,1,1,1,1));
        RuntimeException exception = assertThrows(RuntimeException.class, () ->  userFacade.approveManage("r"+username3,"Nike","r"+username2));
        assertEquals("User is suspended from the system", exception.getMessage());
        userFacade.endSuspendUser("r"+username1,"r"+username3);
        int sizeA=userFacade.getUser("r"+username3).getManagerToApprove().size();
        assertEquals(sizeB,sizeA);
    }

    @Test
    void approveManager_UserAppointNotOwner() {
        try {
            userFacade.suggestManage("r"+username1,"r"+username2,"Adidas",true,false,true,false);
        }
        catch (Exception _){}
        int sizeB=userFacade.getUser("r"+username3).getManagerToApprove().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () ->  userFacade.approveManage("r"+username3,"Adidas","r"+username2));
        assertEquals("User must be Owner", exception.getMessage());
        int sizeA=userFacade.getUser("r"+username3).getManagerToApprove().size();
        assertEquals(sizeB,sizeA);
    }

    @Test
    void approveManager_UserToManageNotLogged() {
        try {
            userFacade.suggestManage("r"+username1,"r"+username2,"Adidas",true,false,true,false);
        }
        catch (Exception _){}
        int sizeB=userFacade.getUser("r"+username2).getManagerToApprove().size();
        userFacade.logout(1,"r"+username2);
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () ->  userFacade.approveManage("r"+username2,"Adidas","r"+username1));
        assertEquals("New Manager user is not logged", exception.getMessage());
        userFacade.login("v1",username2,username2);
        int sizeA=userFacade.getUser("r"+username2).getManagerToApprove().size();
        assertEquals(sizeB,sizeA);
    }

    @Test
    void approveOwner_UserAppointIsManager() {
        try {
            userFacade.suggestManage("r"+username2,"r"+username3,"Nike",true,false,true,false);
            userFacade.getUser("r"+username3).addManagerRole("r"+username2,"Nike");
        }
        catch (Exception _){}
        int sizeB=userFacade.getUser("r"+username2).getManagerToApprove().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () ->  userFacade.approveManage("r"+username3,"Nike","r"+username2));
        assertEquals("User already Manager of this store", exception.getMessage());
        int sizeA=userFacade.getUser("r"+username2).getManagerToApprove().size();
        assertEquals(sizeB,sizeA);
    }

    @Test
    void approveManager_isOwner() {
        try {
            userFacade.suggestManage("r"+username1,"r"+username2,"Adidas",true,false,true,false);
        }
        catch (Exception _){}
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () ->  userFacade.approveManage("r"+username1,"Adidas","r"+username1));
        assertEquals("User cannot be owner of this store", exception.getMessage());
    }

    //AppointManager

    @Test
    void appointManager_Success() {
        try {
            userFacade.suggestManage("r"+username1,"r"+username2,"Adidas",true,false,true,false);
            userFacade.approveManage("r"+username2,"Adidas","r"+username1);
        }
        catch (Exception _){}
        boolean isManagerB=userFacade.getUser("r"+username2).isManager("Adidas");
        assertDoesNotThrow(() -> userFacade.appointManager("r"+username1,"r"+username2,"Adidas",true,false,true,false), "appointManager should not throw any exceptions");
        boolean isManagerA=userFacade.getUser("r"+username2).isManager("Adidas");
        assertEquals(isManagerB,!isManagerA);
    }

    //
    @Test
    void appointManager_StoreNotExist() {
        try {
            userFacade.suggestManage("r"+username1,"r"+username2,"Adidas",true,false,true,false);
            userFacade.approveManage("r"+username2,"Adidas","r"+username1);
        }
        catch (Exception _){}
        boolean isManagerB=userFacade.getUser("r"+username2).isManager("Adidas");
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->  userFacade.appointManager("r"+username1,"r"+username2,"Adidas1",true,false,true,false));
        assertEquals("No store called Adidas1 exist", exception.getMessage());
        boolean isManagerA=userFacade.getUser("r"+username2).isManager("Adidas");
        assertEquals(isManagerB,isManagerA);
    }

    @Test
    void appointManager_UserToOwnerExist() {
        try {
            userFacade.suggestManage("r"+username1,"r"+username2,"Adidas",true,false,true,false);
            userFacade.approveManage("r"+username2,"Adidas","r"+username1);
        }
        catch (Exception _){}
        boolean isManagerB=userFacade.getUser("r"+username2).isManager("Adidas");
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->  userFacade.appointManager("r"+username1,"r"+"username2","Adidas",true,false,true,false));
        assertEquals("No user called rusername2 exist", exception.getMessage());
        boolean isManagerA=userFacade.getUser("r"+username2).isManager("Adidas");
        assertEquals(isManagerB,isManagerA);
    }

    @Test
    void appointManager_AppointIsSuspended() {
        try {
            userFacade.suggestManage("r"+username2,"r"+username3,"Nike",true,false,true,false);
            userFacade.approveManage("r"+username3,"Nike","r"+username2);
        }
        catch (Exception _){}
        boolean isManagerB=userFacade.getUser("r"+username3).isManager("Adidas");
        userFacade.suspendUser("r"+username1,"r"+username2, LocalDateTime.of(2025,1,1,1,1));
        RuntimeException exception = assertThrows(RuntimeException.class, () ->  userFacade.appointManager("r"+username2,"r"+username3,"Nike",true,false,true,false));
        assertEquals("User is suspended from the system", exception.getMessage());
        userFacade.endSuspendUser("r"+username1,"r"+username2);
        boolean isManagerA=userFacade.getUser("r"+username3).isManager("Adidas");
        assertEquals(isManagerB,isManagerA);
    }

    @Test
    void appointManager_UserAppointNotOwner() {
        try {
            userFacade.suggestManage("r"+username2,"r"+username3,"Nike",true,false,true,false);
            userFacade.approveManage("r"+username3,"Nike","r"+username2);
        }
        catch (Exception _){}
        boolean isManagerB=userFacade.getUser("r"+username3).isManager("Nike");
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () ->  userFacade.appointManager("r"+username2,"r"+username3,"Adidas",true,false,true,false));
        assertEquals("Appoint user must be Owner", exception.getMessage());
        boolean isManagerA=userFacade.getUser("r"+username3).isManager("Nike");
        assertEquals(isManagerB,isManagerA);
    }

    @Test
    void appointManager_UserAppointNotLogged() {
        try {
            userFacade.suggestManage("r"+username1,"r"+username2,"Adidas",true,false,true,false);
            userFacade.approveManage("r"+username2,"Adidas","r"+username1);
        }
        catch (Exception _){}
        boolean isManagerB=userFacade.getUser("r"+username2).isManager("Adidas");
        userFacade.logout(0,"r"+username1);
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () ->  userFacade.appointManager("r"+username1,"r"+username2,"Adidas",true,false,true,false));
        assertEquals("Appoint user is not logged", exception.getMessage());
        userFacade.login("v0",username1,username1);
        boolean isManagerA=userFacade.getUser("r"+username2).isManager("Adidas");
        assertEquals(isManagerB,isManagerA);
    }

    @Test
    void appointManager_UserAppointIsManager() {
        try {
            userFacade.suggestManage("r"+username2,"r"+username3,"Nike",true,false,true,false);
            userFacade.approveManage("r"+username3,"Nike","r"+username2);
            userFacade.getUser("r"+username3).addManagerRole("r"+username2,"Nike");
        }
        catch (Exception _){}
        boolean isManagerB=userFacade.getUser("r"+username3).isManager("Nike");
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () ->  userFacade.appointManager("r"+username2,"r"+username3,"Nike",true,false,true,false));
        assertEquals("User already Manager of this store", exception.getMessage());
        boolean isManagerA=userFacade.getUser("r"+username3).isManager("Nike");
        assertEquals(isManagerB,isManagerA);
    }

    @Test
    void approveManager_isManager() {
        try {
            userFacade.suggestManage("r"+username1,"r"+username2,"Adidas",true,false,true,false);
            userFacade.approveManage("r"+username2,"Adidas","r"+username1);
        }
        catch (Exception _){}
        boolean isManagerB=userFacade.getUser("r"+username1).isManager("Adidas");
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () ->  userFacade.appointManager("r"+username1,"r"+username1,"Adidas",true,false,true,false));
        assertEquals("User cannot be owner of this store", exception.getMessage());
        boolean isManagerA=userFacade.getUser("r"+username1).isManager("Adidas");
        assertEquals(isManagerB,isManagerA);
    }































}