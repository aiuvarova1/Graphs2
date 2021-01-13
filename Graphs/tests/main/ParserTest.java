package main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    double res;
    @Test
    void parseSimple() {
        res = Parser.parseDistance("1+4");
        assertEquals(5,res);
    }

    @Test
    void parseSimple1() {
        res = Parser.parseDistance(" 1   +4      ");
        assertEquals(5,res);
    }

    @Test
    void parseSimple2() {
        res = Parser.parseDistance("7*8");
        assertEquals(56,res);
    }

    @Test
    void parseSimple3() {
        res = Parser.parseDistance("5/2");
        assertEquals(2.5,res);
    }
    @Test
    void parseSimple4() {
        //res = Parser.parseDistance("7-9");
        assertThrows(IllegalArgumentException.class,()->Parser.parseDistance("7-9"));
    }

    @Test
    void div0() {
        assertThrows(IllegalArgumentException.class,()->Parser.parseDistance("7/0"));
    }

    @Test
    void checkBrackets() {
        res = Parser.parseDistance("7*(5+3)");
        assertEquals(56,res);
    }

    @Test
    void checkBrackets1() {
        res = Parser.parseDistance("7*(5+3)-5/5+2*3-4");
        assertEquals(57,res);
    }

    @Test
    void checkBrackets2() {
        res = Parser.parseDistance("()()()18*1");
        assertEquals(18,res);
    }

    @Test
    void checkBrackets3() {
        res = Parser.parseDistance("(((15)-(8+4)))");
        assertEquals(3,res);
    }

    @Test
    void checkBrackets4() {
       // res = Parser.parseDistance("()");
        assertThrows(IllegalArgumentException.class,()->Parser.parseDistance("()"));
    }

    @Test
    void checkBrackets5() {
        //res = Parser.parseDistance("((15-7)");
        assertThrows(IllegalArgumentException.class,()->Parser.parseDistance("((15-7)"));
    }

    @Test
    void checkNum() {
        res = Parser.parseDistance("15");
        assertEquals(15,res);
    }
    @Test
    void checkNum1() {
       // res = Parser.parseDistance("0");
        assertThrows(IllegalArgumentException.class,()->Parser.parseDistance("0"));
    }

    @Test
    void bigCheck() {
        res = Parser.parseDistance("0*(13)+ (12-3)-3*2*(12/4)+30%6+12");
        assertEquals(3,res);
    }

    @Test
    void checkFloat() {
        res = Parser.parseDistance("5.6");
        assertEquals(5.6,res);
    }


    @Test
    void checkInf() {
        res = Parser.parseDistance("+\\infty");
        assertEquals(Double.POSITIVE_INFINITY,res);
    }

    @Test
    void checkNegativeInf() {
        res = Parser.parseDistance("-\\infty");
        assertEquals(Double.NEGATIVE_INFINITY,res);
    }
    @Test
    void check() {
        res = Parser.parseDistance("3+3+3+3");
        assertEquals(12,res);
    }

    @Test
    void sqrt() {
        res = Parser.parseDistance("\\sqrt{4}");
        assertEquals(2,res);
    }


    @Test
    void sqrt2() {
       // res = Parser.parseDistance("\\{sqrt{{{}{}4}}");
        assertThrows(IllegalArgumentException.class,()->Parser.parseDistance("\\{sqrt{{{}{}4}}"));
    }

    @Test
    void sqrt3() {
        //res = Parser.parseDistance("{2}");
        assertThrows(IllegalArgumentException.class,()->Parser.parseDistance("{2}"));
    }

    @Test
    void sqrt4() {
        //res = Parser.parseDistance("\\sqrt{-4}");
        assertThrows(IllegalArgumentException.class,()->Parser.parseDistance("\\sqrt{-4}"));
    }

    @Test
    void sqrt5() {
        res = Parser.parseDistance("\\sqrt[3]{27}");
        assertEquals(3,res);
    }

    @Test
    void sqrt6() {
        res = Parser.parseDistance("\\sqrt[3]{27}+9");
        assertEquals(12,res);
    }
    @Test
    void sqrt7() {
        res = Parser.parseDistance("9 +\\sqrt[3]{9*\\sqrt{9}}-9");
        assertEquals(3,res);
    }
    @Test
    void sqrt8() {
        res = Parser.parseDistance("3*(3-1) +\\sqrt[4]{\\sqrt{4}*(6-1)+\\sqrt[3]{27}+3}-1");
        assertEquals(7,res);
    }

    @Test
    void pow() {
        res = Parser.parseDistance("5^{2-1}-1");
        assertEquals(4,res);
    }
    @Test
    void pow1() {
        res = Parser.parseDistance("3+3*2^{4-2*3}");
        assertEquals(3.75,res);
    }

    @Test
    void testFrac1() {
        res = Parser.parseDistance("\\frac{1}{2}");
        assertEquals(0.5,res);
    }

    @Test
    void testFrac2() {
        res = Parser.parseDistance("\\frac{\\frac{4}{2}}{2}");
        assertEquals(1,res);
    }

    @Test
    void testFrac3() {
        res = Parser.parseDistance("2+3*\\frac{5*(5-3)+2}{\\frac{8-2}{1}}");
        assertEquals(8,res);
    }


/////////////////////////////////////////////////////////
    @Test
    void test1() {
        res = Parser.parseDistance("((4-2)*(3+5))/2");
        assertEquals(8,res);
    }

    @Test
    void test2() {
        //res = Parser.parseDistance("{5+2}");
        assertThrows(IllegalArgumentException.class,()->Parser.parseDistance("{5+2}"));
    }



}