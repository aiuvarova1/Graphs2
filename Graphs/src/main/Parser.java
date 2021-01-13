package main;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.stream.Collectors;


public class Parser {

    private static class Token{
        String val;
        Token(String val){
            this.val = val;
        }
    }

    private static class Operation extends Token{
        int priority;

        Operation(char sign, int priority){
            super(String.valueOf(sign));
            this.priority = priority;

        }

        double execute(double x, double y){
            switch(val) {
                case "+":
                    return x+y;
                case "-":
                    return x-y;
                case "*":
                    return x*y;
                case "f":
                case "/":
                    if(y == 0)
                        throw new IllegalArgumentException("Division by zero");
                    return x/y;
                case "%":
                    if(y == 0)
                        throw new IllegalArgumentException("Division by zero");
                    return x%y;
                case "^":
                    return Math.pow(x,y);
                default:
                    return 0;
            }
        }

    }

    private static class Function extends Operation{
        double data;
        int fractionCounter = 0;
        Function(char sign, int priority, double data){
            super(sign, priority);
            this.data = data;
        }

        Function(char sign, int priority){
            super(sign,priority);
        }

        double execute(double x){
            switch(val){
                case "r":
//                    if(data==0 || x==0 || (x<0 && data%2==0))
//                        return -1;
                    if(data == 0)
                        throw new IllegalArgumentException("The root of zero value is invalid");
                    if(x==0)
                        throw new IllegalArgumentException("Can not extract a root of zero");
                    if(x<0 && data%2==0)
                        throw new IllegalArgumentException("Can not extract an even root of a negative value");

                    return Math.pow(x,1.0/data);
                default:
                    return -1;
            }
        }
    }



    private static HashMap<Character,Operation> operations;
    static {
        operations = new HashMap<>();
        operations.put('+',new Operation('+',0));
        operations.put('-',new Operation('-',0));
        operations.put('*',new Operation('*',1));
        operations.put('/',new Operation('/',1));
        operations.put('%',new Operation('%',1));
        operations.put('^',new Operation('^',2));
    }

    /**
     * Parses input into a double
     * @param input entered distance
     * @return incorrect input ? -1 : result distance
     */
    public static double parseDistance(String input){
       // input = input.strip();
        input = input.chars().mapToObj(x->String.valueOf((char)x)).filter(x -> !x.equals(" ")).
                collect(Collectors.joining());

        if(input.equals("\\infty") || input.equals("+\\infty"))
            return Double.POSITIVE_INFINITY;
        if(input.equals("-\\infty"))
            return Double.NEGATIVE_INFINITY;

        ArrayDeque<Token> queue = new ArrayDeque<>();
        ArrayDeque<Token> stack = new ArrayDeque<>();

        createPolandNotation(queue, stack, input);

        double cur;
        Token op;

        ArrayDeque<Double> res = new ArrayDeque<>();
        while(!queue.isEmpty()){
            op = queue.removeFirst();
            try {
                cur = Double.parseDouble(op.val);
                res.addLast(cur);
            }catch(NumberFormatException ex) {

                if(!(op instanceof  Operation) || res.size() < 1)
                    throw new IllegalArgumentException("Not enough arguments");

                double second = res.removeLast();
                if (op.val.equals("r"))
                    res.addLast(((Function)op).execute(second));

                else if (res.size()>=1)
                    res.addLast(((Operation)op).execute(res.removeLast(), second));
            }
        }

        if(res.size() != 1)
            throw new IllegalArgumentException("Not enough operands");
        if(res.getFirst() <= 0)
            throw new IllegalArgumentException("The distance must be a positive number");
        if(res.getFirst() > 100000)
            throw new IllegalArgumentException("The distance must be less than 100000");
//        if(res.size()!= 1 || res.getFirst() <= 0 || res.getFirst() > 200000)
//            return -1;
        return res.getFirst();
    }


    /**
     * Creates a Poland Notation from the input
     * @param queue here the notation itself is stored
     * @param stack keeps operations and braces
     * @param input user's input
     */
    private static void createPolandNotation(ArrayDeque<Token> queue, ArrayDeque<Token> stack,
                                             String input){

        Operation op;
        StringBuilder number = new StringBuilder();
        boolean prevFunc = false;

        if(input.length() == 0)
            throw new IllegalArgumentException("Blank input");

        for(int cur = 0; cur < input.length(); cur++){

            if(prevFunc && input.charAt(cur) != '{' )
                throw new IllegalArgumentException("The function's arguments must be in '{}' parentheses");

            //if digit or point - try to collect a number
            if(Character.isDigit(input.charAt(cur)) || (cur > 0 && input.charAt(cur) == '.' &&
                    Character.isDigit(input.charAt(cur-1)) &&
                    cur < input.length()-1 && Character.isDigit(input.charAt(cur+1))) ||
                    (input.charAt(cur)=='-' && (cur==0 || input.charAt(cur-1)=='(' ||
                            input.charAt(cur-1)=='{'))){

                number.append(input.charAt(cur));

            }else if (input.charAt(cur) == '(' || (prevFunc && input.charAt(cur) == '{')) {
                stack.addLast(new Token(String.valueOf(input.charAt(cur))));
                prevFunc = false;
            }
            else if (input.charAt(cur) == ')'|| input.charAt(cur) == '}') {

                if(number.length() != 0)
                    queue.addLast(new Token(number.toString()));
                number.delete(0,number.length());

                if(input.charAt(cur) == '}'){
                    while(!stack.isEmpty() && !stack.peekLast().val.equals("{")) {
                        if(stack.peekLast() instanceof Function || !(stack.peekLast() instanceof Operation))
                            throw new IllegalArgumentException("'}' has no matching '{'");
                        queue.addLast(stack.removeLast());
                    }
                    if(stack.isEmpty())
                        throw new IllegalArgumentException("'}' has no matching '{'");
                    stack.removeLast();

                    if(stack.getLast().val.equals("f") && ++((Function)(stack.getLast())).fractionCounter < 2){
                            prevFunc = true;
                    }else
                        queue.addLast(stack.removeLast());
                }else {
                    while (!stack.isEmpty() && !stack.peekLast().val.equals("(")) {
                        if(stack.peekLast() instanceof Function || !(stack.peekLast() instanceof Operation))
                            throw new IllegalArgumentException("')' has no matching '('");
                        queue.addLast(stack.removeLast());
                    }
                    if(stack.isEmpty())
                        throw new IllegalArgumentException("')' has no matching '('");
                    stack.removeLast();
                }

            } else {
                if(number.length() != 0)
                    queue.addLast(new Token(number.toString()));
                number.delete(0,number.length());

                if(input.charAt(cur) == '\\'){
                    if(cur + 5 < input.length() &&
                            input.substring(cur,cur+5).equals("\\sqrt")){
                        cur = cur+5;
                        if(input.charAt(cur) == '['){
                            while(cur + 1 < input.length() && Character.isDigit(input.charAt(++cur)))
                                number.append(input.charAt(cur));

                            if(input.charAt(cur) != ']')
                                throw new IllegalArgumentException("'[' has no matching ']'");
                        }else
                            cur--;
                        stack.addLast(new Function('r',2,
                                number.length() == 0 ? 2 : Integer.parseInt(number.toString())));
                        number.delete(0, number.length());
                        prevFunc = true;
                    }else if (cur + 5 < input.length() &&
                            input.substring(cur,cur+5).equals("\\frac")){
                        cur = cur + 4;
                        prevFunc = true;
                        stack.addLast(new Function('f',1));
                    }
                }
                else {
                    op = operations.get(input.charAt(cur));
                    if (op != null) {
                        while (!stack.isEmpty() && stack.peekLast() instanceof Operation &&
                                ((Operation) (stack.peekLast())).priority >= op.priority)
                            queue.addLast(stack.removeLast());
                        stack.addLast(op);
                        if(op == operations.get('^'))
                            prevFunc = true;
                    } else
                        throw new IllegalArgumentException("Invalid operation sign");
                }
            }
        }

        if(number.length() != 0)
            queue.addLast(new Token(number.toString()));

        while(!stack.isEmpty())
        {
            if(stack.peekLast().val.equals("(") || stack.peekLast().val.equals("{"))
                throw new IllegalArgumentException("An opening brace has no pair");
            queue.addLast(stack.removeLast());
        }
    }

}
