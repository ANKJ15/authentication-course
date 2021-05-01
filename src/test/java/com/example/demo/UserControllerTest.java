package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository;
    private CartRepository cartRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Before
    public void setUp() throws Exception {

        userController = new UserController();
        userRepository = Mockito.mock(UserRepository.class);
        cartRepository = Mockito.mock(CartRepository.class);
        bCryptPasswordEncoder = Mockito.mock(BCryptPasswordEncoder.class);

        TestsUtils.injectObjects(userController, "userRepository", userRepository);
        TestsUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestsUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);

    }

    @Test
    public void testCreateUser() {
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("hashedPassword");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newUser");
        request.setPassword("pass1234");
        request.setConfirmPassword("pass1234");
        ResponseEntity<User> response = userController.createUser(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        Assert.assertNotNull(user);
        Assert.assertEquals(0, user.getId());
        Assert.assertEquals("newUser", user.getUsername());
    }

    @Test
    public void testFailCreateUser() {

        String username = "test";
        String password = "";

        Mockito.when(bCryptPasswordEncoder.encode(password)).thenReturn("hashedPassword");

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword(password);
        createUserRequest.setConfirmPassword(password);

        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testFindByUserName() {

        String username = "test1";
        String password = "testPassword";

        Mockito.when(bCryptPasswordEncoder.encode(password)).thenReturn("hashedPassword");

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword(password);
        createUserRequest.setConfirmPassword(password);

        final ResponseEntity<User> userResponseEntity = userController.createUser(createUserRequest);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(userResponseEntity.getBody());

        final ResponseEntity<User> response = userController.findByUserName(username);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();

        assertNotNull(user);
        assertEquals(userResponseEntity.getBody().getId(), user.getId());
        assertEquals(userResponseEntity.getBody().getUsername(), user.getUsername());
        assertEquals(userResponseEntity.getBody().getPassword(), user.getPassword());
    }

    @Test
    public void testFailFindByUserName() {

        String username = "test2";
        String password = "testPassword";

        Mockito.when(bCryptPasswordEncoder.encode(password)).thenReturn("hashedPassword");

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword(password);
        createUserRequest.setConfirmPassword(password);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(null);

        final ResponseEntity<User> response = userController.findByUserName(username);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testFindById() {
        String username = "test3";
        String password = "testPassword";

        Mockito.when(bCryptPasswordEncoder.encode(password)).thenReturn("hashedPassword");

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword(password);
        createUserRequest.setConfirmPassword(password);

        final ResponseEntity<User> userResponseEntity = userController.createUser(createUserRequest);

        Mockito.when(userRepository.findById(0l)).thenReturn(Optional.of(userResponseEntity.getBody()));

        final ResponseEntity<User> response = userController.findById(0l);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();

        assertNotNull(user);
        assertEquals(userResponseEntity.getBody().getId(), user.getId());
    }

    @Test
    public void testFailFindById() {
        String username = "test4";
        String password = "testPassword";

        Mockito.when(bCryptPasswordEncoder.encode(password)).thenReturn("hashedPassword");

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword(password);
        createUserRequest.setConfirmPassword(password);

        Mockito.when(userRepository.findById(0l)).thenReturn(Optional.empty());

        final ResponseEntity<User> response = userController.findById(0l);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
