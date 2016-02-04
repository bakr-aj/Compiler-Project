package lexer;

import java.io.*;
import java.util.*;

public class Lexer {
    
    public enum Type {number,id,keyword,unkown,value};//the type My PL has
    String[] keyword= {"عدد-صحيح","محرف","منطقي","ثابت","=","==","!=","+","/","*",">=","<=","|","&","كرر","اذا","اقراء","اكتب","صح","خطأ","والا","Eof"};//KeyWords
    
    //Token Class
    public class Token{
        String lexim;//the lexim
        Type type;//token type 
        public Token(String _t,Type _ty) {
//           System.out.println(_t);
//           System.out.println(_ty);
            this.lexim = _t;
            this.type = _ty;
        }
        public String getLexim(){
            return this.lexim;
        }
        public Type getType(){
            return this.type;
        }
    }
    
    BufferedReader filebuffer ; //the buffer where the data going to be savd
    String sourceFile ; //path to source code file
    String CodeLine;
    Queue<Token> tokens;//the place where the Token will be stored in order to be parsed
    int lineCount;//indicate number of rad lines 
    
    public Lexer(String _filepath){
        
        sourceFile = _filepath;
        
        //Intializing the buffer reader
        try{
        filebuffer = new BufferedReader(new FileReader(sourceFile));
        }
        catch(FileNotFoundException e){
            System.out.println("File not Found !");
            System.exit(1);
        }
        tokens = new LinkedList<Token>() ;
        lineCount = 0;
    }
    
    //reading one line from the source file
    void readline(){
        try {
            CodeLine = filebuffer.readLine();
            if (CodeLine == null ) {//check the end of file if code line is true then we reached the EoF
                //System.out.println("End of File : "+lineCount);
                CodeLine = "Eof";
            }
        } 
        catch (EOFException e) {
            System.out.println("End of File Exception");
            System.exit(1);
        }catch(IOException e){
            System.out.println("Some error Happend,Sorry");
        }
    }
    //check the lexim type
    Type getType(String _lexim){
        if (_lexim == null || _lexim.length() == 0) {//checking the _lexim to see if we reached the end of file or and empty one
            System.out.println("EoF");
            return null;
            //System.exit(0);
        }
        
        if(Character.isAlphabetic(_lexim.charAt(0)))//if the first char is alphabetic then chech to see if the token is a keyword if not return type is ID
        {
            for (String keyword1 : keyword) {
                if (_lexim.equals(keyword1)) {
                    return Type.keyword;
                }
            }
             return Type.id;

        }
        
        else if (Character.isDigit(_lexim.charAt(0))|| _lexim.charAt(0)=='-' ) {//check if it is a number
            for(int i = 1; i<_lexim.length();i++){
                if(!Character.isDigit(_lexim.charAt(i)))
                    return Type.unkown;
            }
            
            return Type.number;
        }
        else if(!Character.isAlphabetic(_lexim.charAt(0)) && !Character.isDigit(_lexim.charAt(0))){//checking the other casese like the =,+ ...
            for (String tempkeyword : keyword) {
                if (_lexim.equals(tempkeyword)) {
                    return Type.keyword;
                }
            }
            if(_lexim.charAt(0)=='\'' && _lexim.charAt(2)=='\''){//checking the value of a char var type
                return Type.value;
            }
        }
        return Type.unkown;
    }
    
    //splite the line, determine the type then add each token to the queue
    public void scan(){
        readline();
        if(CodeLine != null){
             if (lineCount == 0){
                CodeLine = CodeLine.substring(1, CodeLine.length());
            }
            while(CodeLine.isEmpty()){//this while deals with empty lines
                readline();
            }
        //check the end of file 
           
            if ((CodeLine.charAt(0)=='\\'&&CodeLine.charAt(1)=='\\')) {//deals with commints 
                   readline();
                }
            lineCount++;//++ the nuber of rad lines
            String[] lexims = CodeLine.split("[ ]+");//splite the Code line into tokens 
            for (String lixim : lexims) {
                Type tempType = getType(lixim);//detecting each lexim type 
                Token tempToken = new Token(lixim,tempType);//creating temprary token
                tokens.add(tempToken);//adding the token to the queue to be parsed later
            }
        }
    }
    public Token getToken(){//rerurn the first Token from the queue
        if (tokens.isEmpty()){
            return new Token("EOF", Type.unkown);
        }   
        return tokens.poll();
    }
    public Token lookAhead(){
        if (tokens.isEmpty()){
             return new Token("EOF", Type.unkown);
        }   
        return tokens.peek();
    }
    public boolean isEmpty(){
        return tokens.isEmpty();
    }
}