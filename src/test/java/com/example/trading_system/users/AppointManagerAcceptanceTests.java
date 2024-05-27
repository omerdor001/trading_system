//package com.example.trading_system.users;
//
//import com.example.trading_system.domain.users.Manager;
//import com.example.trading_system.domain.users.Registered;
//import org.junit.jupiter.api.BeforeEach;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.mock;
//
//class AppointManagerAcceptanceTests {
//    Facade facade;
//
//    @BeforeEach
//    void setup(){
//        Registered registered1 = mock(Registered.class);
//        Manager manager1 = mock(Manager.class);
//        facade=mock(Facade.class);
//    }
////
////    @Test
////    @Disabled
////    void appointManager_Success() {
////        when(facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true)).
////                thenReturn(new ResponseEntity<String>("Success appointing manager", HttpStatus.OK));
////        ResponseEntity<String> result=facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true);
////        assertEquals(result,new ResponseEntity<String>("Success appointing manager", HttpStatus.OK));
////    }
////
////    @Test
////    @Disabled
////    void appointManager_AppointeeNotExist() {
////        when(facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true)).
////                thenReturn(new ResponseEntity<>("No user called testAppointee exist", HttpStatus.BAD_REQUEST));
////        ResponseEntity<String> result=facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true);
////        assertEquals(new ResponseEntity<>("No user called testAppointee exist", HttpStatus.BAD_REQUEST),result);
////    }
////
////    @Test
////    @Disabled
////    void appointManager_newManagerNotExist() {
////        when(facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true)).
////                thenReturn(new ResponseEntity<>("No user called testNewManager exist", HttpStatus.BAD_REQUEST));
////        ResponseEntity<String> result=facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true);
////        assertEquals(new ResponseEntity<>("No user called testNewManager exist", HttpStatus.BAD_REQUEST),result);
////    }
////
////    @Test
////    @Disabled
////    void appointManager_appointeeNotOwner() {
////        when(facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true)).
////                thenReturn(new ResponseEntity<>("User must be Owner", HttpStatus.BAD_REQUEST));
////        ResponseEntity<String> result=facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true);
////        assertEquals(new ResponseEntity<>("User must be Owner", HttpStatus.BAD_REQUEST),result);
////    }
////
////    @Test
////    @Disabled
////    void appointManager_appointeeNotLogged() {
////        when(facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true)).
////                thenReturn(new ResponseEntity<>("Appoint user is not logged", HttpStatus.BAD_REQUEST));
////        ResponseEntity<String> result=facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true);
////        assertEquals(new ResponseEntity<>("Appoint user is not logged", HttpStatus.BAD_REQUEST),result);
////    }
////
////    @Test
////    @Disabled
////    void appointManager_newManagerAlreadyManager() {
////        when(facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true)).
////                thenReturn(new ResponseEntity<>("User already Manager of this store", HttpStatus.BAD_REQUEST));
////        ResponseEntity<String> result=facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true);
////        assertEquals(new ResponseEntity<>("User already Manager of this store", HttpStatus.BAD_REQUEST),result);
////    }
////
////    @Test
////    @Disabled
////    void appointManager_newManagerAlreadyOwner() {
////        when(facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true)).
////                thenReturn(new ResponseEntity<>("User cannot be owner of this store", HttpStatus.BAD_REQUEST));
////        ResponseEntity<String> result=facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true);
////        assertEquals(new ResponseEntity<>("User cannot be owner of this store", HttpStatus.BAD_REQUEST),result);
////    }
//
//
//}