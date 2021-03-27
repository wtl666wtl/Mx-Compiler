import AST.RootNode;
import Assembly.*;
import Backend.*;
import MIR.*;
import Frontend.*;
import Parser.*;
import Util.MxErrorListener;
import Util.error.error;
import Util.scope.globalScope;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;


public class Main {
    public static void main(String[] args) throws Exception{

        boolean CodeGen = true;
        if (args.length > 0) {
            for (String arg : args) {
                switch (arg) {
                    case "-semantic": CodeGen = false;break;
                    default: break;
                }
            }
        }

        //String name = "C:\\Users\\23510\\Downloads\\Compiler-2021-testcases\\codegen\\t68.mx";
        //String name = "C:\\Users\\23510\\Downloads\\Compiler-2021-testcases\\sema\\basic-package\\basic-64.mx";
        //InputStream input = new FileInputStream(name);

        InputStream input = System.in;
        try {
            RootNode ASTRoot;
            globalScope gScope = new globalScope();

            MxLexer lexer = new MxLexer(CharStreams.fromStream(input));
            lexer.removeErrorListeners();
            lexer.addErrorListener(new MxErrorListener());
            MxParser parser = new MxParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(new MxErrorListener());
            ParseTree parseTreeRoot = parser.program();
            ASTBuilder astBuilder = new ASTBuilder();
            ASTRoot = (RootNode)astBuilder.visit(parseTreeRoot);

            rootNode rt = new rootNode();

            new SymbolCollector(gScope, rt).visit(ASTRoot);
            new TypeCollector(gScope).visit(ASTRoot);
            new SemanticChecker(gScope, rt).visit(ASTRoot);

            if(CodeGen) {
                new IRBuilder(gScope, rt).visit(ASTRoot);
                new Mem2Reg(rt).work();
                new SolvePhi(rt).run();

                AsmRootNode AsmRt = new AsmRootNode();
                new InstSelector(AsmRt).visitRt(rt);
                new RegAlloc(AsmRt).work();

                PrintStream pst = new PrintStream("output.s");
                new AsmPrinter(AsmRt, pst).print();
            }
        } catch (error er) {
            System.err.println(er.toString());
            throw new RuntimeException();
        }
    }
}