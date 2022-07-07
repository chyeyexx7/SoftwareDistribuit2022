import org.junit.Test;
import static org.junit.Assert.*;
import model.GameMode;

/**
 * Tests for gamemode
 */
public class GameModeTest {

    @Test
    public void get_manual_mode_test(){
        assertEquals("MANUAL",  GameMode.MANUAL.toString());
    }

    @Test
    public void get_automatic_mode_test(){
        assertEquals("AUTOMATIC",  GameMode.AUTOMATIC.toString());
    }

}
