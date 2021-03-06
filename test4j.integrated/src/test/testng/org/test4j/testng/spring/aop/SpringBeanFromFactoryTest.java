package org.test4j.testng.spring.aop;

import java.util.Arrays;
import java.util.List;

import org.test4j.fortest.beans.User;
import org.test4j.fortest.service.UserDao;
import org.test4j.fortest.service.UserService;
import org.test4j.module.core.utility.MessageHelper;
import org.test4j.module.database.IDatabase;
import org.test4j.module.spring.ISpring;
import org.test4j.module.spring.annotations.SpringContext;
import org.test4j.testng.Test4J;
import org.testng.annotations.Test;

@Test
@SpringContext({ "org/test4j/module/spring/testedbeans/aop/proxybeans.xml",
        "org/test4j/module/spring/testedbeans/xml/data-source.xml" })
public class SpringBeanFromFactoryTest extends Test4J implements IDatabase, ISpring {

    UserDao userDao = new UserDao() {
                        @Override
                        public List<User> findUserByPostcode(String postcode) {
                            return null;
                        }

                        @Override
                        public void insertUser(User user) {

                        }

                        @Override
                        public List<User> findAllUser() {
                            MessageHelper.info("find all user");
                            return Arrays.asList(new User(), new User());
                        }

                        @Override
                        public int partialNotMock() {
                            return 0;
                        }

                    };

    public void test() {
        UserService userService = spring.getBean("userService");
        List<User> users = userService.findAllUser();
        want.collection(users).sizeEq(2);
    }
}
