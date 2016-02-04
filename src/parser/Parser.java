package parser;
import java.util.*;
import lexer.*;
import lexer.Lexer.*;

public class Parser {
    
    enum symbleType {character,integer,bool,unkown};
    
    class Symbole extends Object{
        symbleType type;
        String lexim;
        int address;
        boolean constant=false;
        
        public Symbole(symbleType _type,String _lexim) {
            this.type = _type;
            this.lexim = _lexim;
            store();
        }
        private void store(){
            if (type == symbleType.bool){
                address+=1;
            }
            else if (type == symbleType.character){
              address+= 1;  
            }
            else if(type == symbleType.integer){
                address += 2;
            }
        }
        public void setConstant(){
            this.constant = true;
        }
        public boolean isConstant(){
            return this.constant;
        }
        public void makeConstatn (){
            this.constant = true;
        }
        public symbleType getType(){
            return this.type;
        }
        public String getLexim(){
            return this.lexim;
        }
        @Override
        public boolean equals(Object _lexim){
                if (_lexim == null) {
                    return false;
                }
                if (getClass() != _lexim.getClass()) {
                    return false;
                }
            if(((Symbole)_lexim).getLexim().equalsIgnoreCase(this.lexim))
                return true;
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 71 * hash + Objects.hashCode(this.lexim);
            return hash;
        }
    }
    
    Lexer lexer;
    int tempNameCounter;
    LinkedList<LinkedList<Symbole>> symboleTable;
    
    public Parser(String _path) {
        symboleTable = new LinkedList<>();
        symboleTable.add(new LinkedList<Symbole>());
        this.lexer =new Lexer(_path);
        lexer.scan();
    }
    private Symbole searchSymboleTable(String _lexim){//searaching for predefined vars in all scopes
        for(LinkedList l : symboleTable){
            for (Object sy : l) {
                if (((Symbole)sy).getLexim().equals(_lexim)) {
                    return ((Symbole)sy);
                }
            }
        }
        return null;
    }
    private boolean varInThisScope (Symbole s){//searching for predefined vars in this scope using override equals 
        Object[] symbole = symboleTable.peek().toArray();
        for (Object symbole1 : symbole) {
            if(((Symbole)symbole1).equals(s))
                return true;
        }
        
        return false;
    }
    private boolean varInThisScope (String s){//searching for predefined vars in this using predicate
        if (symboleTable.peek().stream().anyMatch((sym) -> (sym.getLexim().equals(s)))) {
            return true;
        }
        return false;
    }
    public void compile(){//compile -> reapte | ifStmt | write | read | decl | assg
        Lexer.Token t = lexer.lookAhead();
        switch (t.getLexim()) {
            case "كرر":
                repeat();
                break;
            case "اذا":
                ifStmt();
                break;
            case "اكتب":
                write();
                break;
            case "اقراء":
                read();
                break;
            case "عدد-صحيح":
            case "محرف":
            case "منطقي":
                decl();
                break;
            default:
                if(t.getType() == Lexer.Type.id)
                    assg(lexer.getToken(),1);
                //decl();
        }
    }
    private void repeat(){//repeat ->REPEAT ( expr ) {\n stmt } \n stms
        lexer.getToken();
        String Ecode = "repeat untile ";
        Lexer.Token temp = lexer.getToken();
        if (!temp.getLexim().equals("(")) {
            System.out.println("( Statment expected in Repeat");
            System.exit(2);
        }
        Ecode += expr();
        Ecode += " is false ";
        System.out.println(Ecode);
        temp = lexer.getToken();
        if (!temp.getLexim().equals(")")) {
            System.out.println(") Statment expected ");
            System.exit(2);
        }
        temp = lexer.getToken();
        symboleTable.addFirst(new LinkedList<Symbole>());
        if (!temp.getLexim().equalsIgnoreCase("{")) {
            System.out.println("{ Statment expected");
            System.exit(2);
        }
        lexer.scan();
        temp = lexer.lookAhead();
        if (!temp.getLexim().equalsIgnoreCase("}")) {
            this.compile();
        }
        temp = lexer.getToken();
        if (temp.getLexim().equalsIgnoreCase("}")) {//parsing the statment after the }  
            //System.out.println(symboleTable.size()+"  "+symboleTable.peek().size()); //to check the symbole table
            System.out.println("End of Repeat");
            symboleTable.remove();
            //System.out.println(symboleTable.size()+"  "+symboleTable.peek().size());
            lexer.scan();
            this.compile();
        }else{
            System.out.println("} expected");
            System.exit(2);
        }
        
    }
    private void ifStmt(){//repeat ->IF ( expr ) {\n stmt } \n stms
        lexer.getToken();   
        Lexer.Token temp = lexer.getToken();
        String Ecode = "If ";
        if (!temp.getLexim().equals("(")) {
            System.out.println(") Statment expected in if");
            System.exit(2);
        }
        Ecode += expr()+" is true do ";
        System.out.println(Ecode);
        temp = lexer.getToken();
        if (!temp.getLexim().equals(")")) {
            System.out.println(") Statment expected");
            System.exit(2);
        }
        temp = lexer.getToken();
        if (!temp.getLexim().equalsIgnoreCase("{")) {
            System.out.println("{ Statment expected");
            System.exit(2);
        }
        symboleTable.addFirst(new LinkedList<>());
        lexer.scan();
        temp = lexer.lookAhead();
        if (!temp.getLexim().equalsIgnoreCase("}")) {
            this.compile();
        }
        temp = lexer.getToken();
        if (temp.getLexim().equalsIgnoreCase("}")) {//parsing the statment after the }
            System.out.println("End of if");
            symboleTable.remove();
            lexer.scan();
            this.compile();
        }else{
            System.out.println("} expected");
            System.exit(2);
        }
        if("والا".equals(lexer.lookAhead().getLexim())){
            System.out.println("else do ");
            lexer.getToken();
            if (!lexer.getToken().getLexim().equalsIgnoreCase("{")) {
                System.out.println("{ Statment expected ");
                System.exit(2);
            }
            symboleTable.addFirst(new LinkedList<>());
            lexer.scan();
            temp = lexer.lookAhead();
            if (!temp.getLexim().equalsIgnoreCase("}")) {
                this.compile();
            }
            temp = lexer.getToken();
            if (temp.getLexim().equalsIgnoreCase("}")) {//parsing the statment after the }
                System.out.println("End of else");
                symboleTable.remove();
                
                lexer.scan();
                this.compile();
            }
        }
    }
    private void write(){
        System.out.print("Write ");
        lexer.getToken();
        System.out.println(expr());
        lexer.scan();
        if(!lexer.isEmpty())
            this.compile();
    }
    private void read(){
        System.out.print("Read ");
        lexer.getToken();
        if(lexer.lookAhead().getType() != Lexer.Type.id){
            System.exit(2);
        }
        if (searchSymboleTable(lexer.lookAhead().getLexim())==null){
            System.exit(2);
        }
        System.out.println(lexer.getToken().getLexim());
        lexer.scan();
        if(!lexer.isEmpty())
            this.compile();
    }
    private void decl(){//decl -> varType ID | varType ID const = VAlUE
        symbleType type = symbleType.unkown;
        String lex;
        Lexer.Token tempToken;
        switch (lexer.lookAhead().getLexim()) {
            case "عدد-صحيح":
                type = symbleType.integer;
                break;
            case "محرف":
                type = symbleType.character;
                break;
            case "منطقي":
                type = symbleType.bool;
                break;
            default :
                return;
        }
        lexer.getToken();
        
        if(lexer.lookAhead().getType()== Lexer.Type.keyword && lexer.lookAhead().getLexim().equalsIgnoreCase("ثابت"))
        {
            lexer.getToken();
            tempToken = lexer.getToken();//getting the var
            if(tempToken.getType() == Lexer.Type.id){
                lex =tempToken.getLexim();
                if(searchSymboleTable(lex)==null){
                    if(!lexer.lookAhead().getLexim().equalsIgnoreCase("=")){
                        System.out.println("you can not define a constant with out value = is missing !");
                        System.exit(2);
                    }
                    symboleTable.peek().add(new Symbole(type, lex));
                    symboleTable.peek().peek().makeConstatn();
                    assg(tempToken,0);
                    
                }
                else{
                    System.out.println("The variable is already decleared !");
                    System.exit(2);
                }
            lexer.scan();
            if(!lexer.isEmpty())
            this.compile();
                
            }
        }
        else {
            tempToken = lexer.getToken();
            if(tempToken.getType() == Lexer.Type.id){
                lex =tempToken.getLexim();
                if(searchSymboleTable(lex)==null){
                    symboleTable.peek().add(new Symbole(type, lex));
                }
                else{
                    System.out.println("The variable is already decleared !");
                    System.exit(2);
                }
                lexer.scan();
                if(!lexer.isEmpty())
                    this.compile();

            }
        }
                
    }
    private void assg(Token t,int runcount){//assg -> ID = ID | CHAR | expr | RWONG | RIGHT
//        Token t = lexer.getToken();
        //System.out.println("asar ");
        if (searchSymboleTable(t.getLexim())== null ){
            System.out.println("var undefined "+t.getLexim());
            System.exit(2);
        }
        if (runcount !=0 && searchSymboleTable(t.getLexim()).isConstant()) {
            System.out.println("This is a constatn var you can not change it's value : "+t.getLexim());
            System.exit(2);
        }
        if(!lexer.getToken().getLexim().equalsIgnoreCase("=")){
            System.out.println("= expected");
            System.exit(2);
        }
        if (lexer.lookAhead().getType() == Lexer.Type.id) {//= ID
            if (searchSymboleTable(lexer.lookAhead().getLexim()).getType()!= null){//is the other var defined
                if (searchSymboleTable(lexer.getToken().getLexim()).getType()!= searchSymboleTable(t.getLexim()).getType()) {//matching vars types
                    System.out.println("Uncompatible types");
                    System.exit(2);
                }
            }else{
                System.out.println("assinging Undefined var ");
                System.exit(2);
            }  
        }
        else {
            if (searchSymboleTable(t.getLexim()).getType()== symbleType.character) {// CHAR
                if(lexer.getToken().getType() != Type.value){
                    System.out.println("char expected");
                    System.exit(2);
                }
            }
            else if (searchSymboleTable(t.getLexim()).getType()== symbleType.bool){//WRONG | WRIGHT
                if(!lexer.lookAhead().getLexim().equalsIgnoreCase("صح")&&!lexer.lookAhead().getLexim().equalsIgnoreCase("خطأ")){
                    System.out.println("boolean expected");
                    System.exit(2);
                }
                lexer.getToken();
            }
            else if (searchSymboleTable(t.getLexim()).getType()== symbleType.integer){// Integer
            expr();
            }
            else{
                System.out.println("Error in assignment");
                System.exit(2);
            }
        }
       lexer.scan();
       if(!lexer.isEmpty())
            this.compile();
    }
    private String expr(){//expr -> simpleExpr comparisonOp simpleExpr | simpleExpr | notOp expr
        String leftVal = simpleExpr();
        String newTemp = getNewTemp();
        if(comparisonOp())
            {
                String op = lexer.getToken().getLexim();
                String rightVal = simpleExpr();
                System.out.printf("%s = %s %s %s \n",newTemp,leftVal,op,rightVal);
                return newTemp;
            }
            else 
               return leftVal; 
        
    }
    private String simpleExpr(){//sempleExpr -> term simpleExprPrime
       String val= term();
       return  simpleExprPrime(val);

    }
    private String simpleExprPrime(String _val){//simpleExprPrime -> addop term simpleExprPrime | E
        if(addop()){
            String op= lexer.getToken().getLexim();
            String val = term();
            String newtemp = getNewTemp();
            System.out.printf("%s = %s %s %s \n",newtemp,_val,op,val);
            return simpleExprPrime(newtemp);
        }
        return _val;
    }
    private String term(){//term -> factor termPrime
        String fact = factor();
        return termPrime(fact);
       
    }
    private String termPrime(String _val){//termPrime -> mulop factor termPrime | E
        if(mulop()){
            String op= lexer.getToken().getLexim();
            String newtemp = getNewTemp();
            String rightval = factor();
            System.out.printf("%s = %s %s %s \n",newtemp,_val,op,rightval);
            return termPrime(newtemp);
        }
        return _val;
    }
    private boolean comparisonOp(){//comariosnOp -> >= | <= | < | > | == | != 
        if(!lexer.isEmpty())
            if(lexer.lookAhead().getLexim().equals(">")||lexer.lookAhead().getLexim().equals("<"))
            {
                return true;
            }
            else if (lexer.lookAhead().getLexim().equals(">=")||lexer.lookAhead().getLexim().equals("<=")) {
                
                return true;
            }
        else if (lexer.lookAhead().getLexim().equals("==")||lexer.lookAhead().getLexim().equals("!=")) {
               
                return true;
            }
        return false;
    }
    private boolean addop(){//addop -> + | -
        if(!lexer.isEmpty())
            if(lexer.lookAhead().getLexim().equals("+")||lexer.lookAhead().getLexim().equals("-"))
            {
                return true;
            }
        return false;
    }
    private boolean mulop(){//mulop -> * | /
        if(!lexer.isEmpty())
            if(lexer.lookAhead().getLexim().equals("*")||lexer.lookAhead().getLexim().equals("/"))
            {
                
                return true;
            }
        return false;
    }
    private String factor(){
        Lexer.Token temp = lexer.lookAhead();
        if(temp.getLexim().equals("(")){//factor -> (expr)
            lexer.getToken();
            String val = expr();
            temp = lexer.getToken();
            if(!temp.getLexim().equals(")"))
            {
                System.out.println(") expected");
                System.exit(2);
            }
            return val;
        }
        else if(temp.getType() == Lexer.Type.id){//factor -> id
            if (searchSymboleTable(temp.getLexim())!= null) {
                if(searchSymboleTable(temp.getLexim()).getType() != symbleType.integer && searchSymboleTable(temp.getLexim()).getType() != symbleType.bool){
                    System.out.println("You can not use non integer vars in an exprasion  : "+temp.getLexim());
                    System.exit(2);
                }
                return lexer.getToken().getLexim();
            }
            System.out.println(lexer.getToken().getLexim()+"  is not defined");
            System.exit(2);//the variable is not defined 
        }
        else if(temp.getType() == Lexer.Type.number){//factor -> number
            
            return lexer.getToken().getLexim();
        }
        System.exit(2);
        return "Error";
    }
    private String getNewTemp(){
        String tname = "t"+tempNameCounter;
        tempNameCounter++;
        return tname;
    }
}