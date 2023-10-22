package lexer_package;
import lexer_package.*;
public class Lexer {
    private final String input;
    private int pos = 0;
    private Token curToken;
    private int value;

    public Lexer(String input) {
        this.input = input;
        this.next();
    }

    public int getValue() {
        return this.value;
    }

    //是否是标识符中的合法字符
    private boolean isIdentChar(char c) {
        if (Character.isDigit(c) || c == '_' || Character.isLetter(c)) {
            return true;
        }
        return false;
    }

    private boolean match(String str, Category category) {
        int len = str.length();
        if (pos + len <= input.length() &&
                input.substring(pos, pos + len).equals(str) &&
                (pos + len == input.length() || !isIdentChar(input.charAt(pos + len)))) {
            //读入接下来五个字符是break 并且break和之后的第一个字符不构成ident
            curToken = new Token(category, str);
            pos += len;
            return true;
        }
        return false;
    }

    private boolean matchNonLetter(String str, Category category) {
        int len = str.length();
        if (pos + len <= input.length() &&
                input.substring(pos, pos + len).equals(str)) {
            curToken = new Token(category, str);
            pos += len;
            return true;
        }
        return false;
    }

    private void matchOne(char c, Category category) {
        curToken = new Token(category, String.valueOf(c));
        pos++;
    }

    public String getIndent() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && this.isIdentChar(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            pos++;
        }
        return sb.toString();
    }

    private String getNumber() {
        //从当前位置起获得一个数字（可能是多位）
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            ++pos;//这个地方让pos++了（重要）
        }

        return sb.toString();
    }

    public void next() {
        if (pos == input.length()) {
            this.value = -1;
            return;
        }
        char c = input.charAt(pos);
        while (c == ' ' || c == '\t') {
            pos++;
            if (pos == input.length()) {
                this.value = -1;
                return;
            }
            c = input.charAt(pos);
        }
        if (Character.isLetter(c)) {
            boolean flag = false;
            switch (c) {
                case 'b':
                    flag = this.match("break", Category.BREAKTK);
                    break;
                case 'c':
                    flag = this.match("const", Category.CONSTTK) ||
                            this.match("continue", Category.CONTINUETK);
                    break;
                case 'e':
                    flag = this.match("else", Category.ELSETK);
                    break;
                case 'f':
                    flag = this.match("for", Category.FORTK);
                    break;
                case 'g':
                    flag = this.match("getint", Category.GETINTTK);
                    break;
                case 'i':
                    flag = this.match("int", Category.INTTK) ||
                            this.match("if", Category.IFTK);
                    break;
                case 'm':
                    flag = this.match("main", Category.MAINTK);
                    break;
                case 'p':
                    flag = this.match("printf", Category.PRINTFTK);
                    break;
                case 'r':
                    flag = this.match("return", Category.RETURNTK);
                    break;
                case 'v':
                    flag = this.match("void", Category.VOIDTK);
                    break;
            }
            if (!flag) {
                String str = this.getIndent();
                curToken = new Token(Category.IDENFR, str);
            }
        }
        if (Character.isDigit(c)) {
            String numStr = this.getNumber();
            curToken = new Token(Category.INTCON, numStr);
        }
        if (!Character.isLetter(c) && !Character.isDigit(c)) {
            //不是数字也不是字母
            boolean error = false;//错误处理
            switch (c) {
                case '&':
                    error = this.matchNonLetter("&&", Category.AND);
                    break;
                case '!':
//                    error = this.matchNonLetter("!", Category.NOT) ||
//                            this.matchNonLetter("!=", Category.NEQ);
                    error = this.matchNonLetter("!=", Category.NEQ) ||
                            this.matchNonLetter("!", Category.NOT);
                    break;
                case '|':
                    error = this.matchNonLetter("||", Category.OR);
                    break;
                case '<':
//                    error = this.matchNonLetter("<", Category.LSS)
//                            || this.matchNonLetter("<=", Category.LEQ);
                    error = this.matchNonLetter("<=", Category.LEQ)
                            || this.matchNonLetter("<", Category.LSS);
                    break;
                case '>':
//                    error = this.matchNonLetter(">", Category.GRE) ||
//                            this.matchNonLetter(">=", Category.GEQ);
                    error = this.matchNonLetter(">=", Category.GEQ) ||
                            this.matchNonLetter(">", Category.GRE);
                    break;
                case '=':
                    error = this.matchNonLetter("==", Category.EQL) ||
                            this.matchNonLetter("=", Category.ASSIGN);
                    break;
                case '+':
                    this.matchOne(c, Category.PLUS);
                    break;
                case '-':
                    this.matchOne(c, Category.MINU);
                    break;
                case '*':
                    this.matchOne(c, Category.MULT);
                    break;
                case '/':
                    //注释
                    if (pos + 1 < input.length() && input.charAt(pos + 1) == '/') {
                        //预读一个字符 如果是//直接结束
                        this.value = -1;
                        return;
                    }
                    if (pos + 1 < input.length() && input.charAt(pos + 1) == '*') {
                        //读到/*
                        pos += 2;
                        //在这一行中读到*/时tempFlag为True
                        boolean tempFlag = false;
                        while (pos + 1 < input.length()) {
                            if (input.charAt(pos) == '*' &&
                                    input.charAt(pos + 1) == '/') {
                                pos += 2;
                                tempFlag = true;
                                break;
                            }
                            pos++;
                        }
                        if (tempFlag) {
                            if (pos == input.length()) {
                                this.value = -1;
                                return;
                            }
                        } else {
                            this.value = -2;
                            return;
                        }
                    }
                    this.matchOne(c, Category.DIV);
                    break;
                case '%':
                    this.matchOne(c, Category.MOD);
                    break;
                case ';':
                    this.matchOne(c, Category.SEMICN);
                    break;
                case ',':
                    this.matchOne(c, Category.COMMA);
                    break;
                case '(':
                    this.matchOne(c, Category.LPARENT);
                    break;
                case ')':
                    this.matchOne(c, Category.RPARENT);
                    break;
                case '[':
                    this.matchOne(c, Category.LBRACK);
                    break;
                case ']':
                    this.matchOne(c, Category.RBRACK);
                    break;
                case '{':
                    this.matchOne(c, Category.LBRACE);
                    break;
                case '}':
                    this.matchOne(c, Category.RBRACE);
                    break;
                case '"':
                    StringBuilder sb = new StringBuilder();
                    sb.append("\"");
                    pos++;
                    while (true) {
                        char temp = input.charAt(pos);
                        sb.append(temp);
                        if (temp == '"') {
                            break;
                        } else {
                            pos++;
                        }
                    }
                    curToken = new Token(Category.STRCON, sb.toString());
                    pos++;
                    break;
                case '_':
                    String str = this.getIndent();
                    curToken = new Token(Category.IDENFR, str);
                    break;
                default:
                    System.out.println(String.valueOf(c) + "!an error has happened");
            }
        }
        this.value = 0;
        return;
    }

    public String peek() {
        return this.curToken.toString();
    }
    public Token peekToken(){
        return this.curToken;
    }
}
