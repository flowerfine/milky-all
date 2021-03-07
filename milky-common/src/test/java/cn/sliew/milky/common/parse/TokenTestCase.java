package cn.sliew.milky.common.parse;

import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

public class TokenTestCase extends MilkyTestCase {


    @Test
    public void test() {
        Token token = Token.root("type", "value");
        System.out.println(token.toString());
    }
}
