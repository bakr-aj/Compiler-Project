package cd_project;
import parser.Parser;

public class CD_Project {

    public static void main(String[] args) {
        
        Parser p = new Parser("SorceCode.txt");
        p.compile();
    }
    
}

/*
"SorceCode.txt"
1- improve type checking by spliting the function getType into function"recusive".
2- every keyword check should be done using the defined enum in lexer
Exit code number 1 from lexer.
                 2 from Parser.
*/
