import org.junit.Test;
import static org.junit.Assert.*;
import utils.Protocol;

public class ProtocolTest {

    @Test
    public void get_hello_code_test() {
        assertEquals(1, Protocol.HELLO.getCode());
    }

    @Test
    public void get_ready_code_test() {
        assertEquals(2, Protocol.READY.getCode());
    }

    @Test
    public void get_play_code_test() { assertEquals(3, Protocol.PLAY.getCode()); }

    @Test
    public void get_admit_code_test() {
        assertEquals(4, Protocol.ADMIT.getCode());
    }

    @Test
    public void get_word_code_test() {
        assertEquals(5, Protocol.WORD.getCode());
    }

    @Test
    public void get_result_code_test() {
        assertEquals(6, Protocol.RESULT.getCode());
    }

    @Test
    public void get_stats_code_test() {
        assertEquals(7, Protocol.STATS.getCode());
    }

    @Test
    public void get_error_code_test() {
        assertEquals(8, Protocol.ERROR.getCode());
    }

    @Test
    public void get_hello_name_test() {
        assertEquals("HELLO", Protocol.HELLO.getName());
    }

    @Test
    public void get_ready_name_test() {
        assertEquals("READY", Protocol.READY.getName());
    }

    @Test
    public void get_play_name_test() {
        assertEquals("PLAY", Protocol.PLAY.getName());
    }

    @Test
    public void get_admit_name_test() {
        assertEquals("ADMIT", Protocol.ADMIT.getName());
    }

    @Test
    public void get_word_name_test() {
        assertEquals("WORD", Protocol.WORD.getName());
    }

    @Test
    public void get_result_name_test() {
        assertEquals("RESULT", Protocol.RESULT.getName());
    }

    @Test
    public void get_stats_name_test() {
        assertEquals("STATS", Protocol.STATS.getName());
    }

    @Test
    public void get_error_name_test() {
        assertEquals("ERROR", Protocol.ERROR.getName());
    }

    @Test
    public void get_hello_com_by_code_test() {
        assertEquals(Protocol.HELLO, Protocol.getComByCode((byte) 1));
    }

    @Test
    public void get_ready_com_by_code_test() {
        assertEquals(Protocol.READY, Protocol.getComByCode((byte) 2));
    }

    @Test
    public void get_play_com_by_code_test() {
        assertEquals(Protocol.PLAY, Protocol.getComByCode((byte) 3));
    }

    @Test
    public void get_admit_com_by_code_test() {
        assertEquals(Protocol.ADMIT, Protocol.getComByCode((byte) 4));
    }

    @Test
    public void get_word_com_by_code_test() {
        assertEquals(Protocol.WORD, Protocol.getComByCode((byte) 5));
    }

    @Test
    public void get_result_com_by_code_test() {
        assertEquals(Protocol.RESULT, Protocol.getComByCode((byte) 6));
    }

    @Test
    public void get_stats_com_by_code_test() {
        assertEquals(Protocol.STATS, Protocol.getComByCode((byte) 7));
    }

    @Test
    public void get_error_com_by_code_test() {
        assertEquals(Protocol.ERROR, Protocol.getComByCode((byte) 8));
    }

}