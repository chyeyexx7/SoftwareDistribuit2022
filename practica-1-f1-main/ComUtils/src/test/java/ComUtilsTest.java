import org.junit.Test;
import static org.junit.Assert.*;
import utils.ComUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ComUtilsTest {

    @Test
    public void example_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            comUtils.write_int32(2);
            comUtils.write_string_variable("Chang");
            comUtils.write_byte((byte) 0);
            int readedInt = comUtils.read_int32();

            assertEquals(2, readedInt);
            assertEquals("Chang", comUtils.read_string_variable());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void read_write_int32_test() {
        File file = new File("test");
        int read_int32;
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));

            comUtils.write_int32(5);
            read_int32 = comUtils.read_int32();

            assertEquals(5,read_int32);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void read_write_char_test() {
        File file = new File("test");
        char read_char;
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));

            comUtils.write_char('o');
            read_char = comUtils.read_char();

            assertEquals('o',read_char);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void read_write_string_variable_test() {
        File file = new File("test");
        String a = "jorge vinagre";
        String read;
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));

            comUtils.write_string_variable(a);
            comUtils.write_byte((byte) 0);
            read = comUtils.read_string_variable();
            assertEquals("jorge vinagre", read);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void read_write_bytes_test() throws InvocationTargetException, FileNotFoundException {
        File file = new File("test");
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            comUtils.write_byte((byte) 5);

            Method method = ComUtils.class.getDeclaredMethod("read_bytes", int.class);
            method.setAccessible(true);
            byte readByte = ((byte[])method.invoke(comUtils, 1))[0];

            assertEquals((byte) 5, readByte);

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void read_write_word_test() {
        File file = new File("test");
        String a = "JORGE";
        String read;
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));

            comUtils.write_word(a);
            read = comUtils.read_word();
            assertEquals("JORGE", read);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
