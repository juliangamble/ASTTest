package ast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

/**
 * Eclipse refactoring demo -  programmatically adds imports to other file
 * @author julian
 *
 */
public class EclipseRefactoringAPIDemo {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		EclipseRefactoringAPIDemo test = new EclipseRefactoringAPIDemo();
		test.processJavaFile(new File("./src/ast/Readable.java"));
	}

	
	public void processJavaFile(File file) throws IOException, MalformedTreeException, BadLocationException {
	    String source = FileUtils.readFileToString(file);
	    Document document = new Document(source);
	    ASTParser parser = ASTParser.newParser(AST.JLS3);
	    parser.setSource(document.get().toCharArray());
	    CompilationUnit unit = (CompilationUnit)parser.createAST(null);
	    unit.recordModifications();

	    // to get the imports from the file
	    List<ImportDeclaration> imports = unit.imports();
	    for (ImportDeclaration i : imports) {
	        System.out.println(i.getName().getFullyQualifiedName());
	    }

	    // to create a new import
	    AST ast = unit.getAST();
	    ImportDeclaration id = ast.newImportDeclaration();
	    String classToImport = "ast.Readable";
	    
	    id.setName(ast.newName(classToImport.split("\\.")));
	    unit.imports().add(id); // add import declaration at end

	    // to save the changed file
	    TextEdit edits = unit.rewrite(document, null);
	    edits.apply(document);
	    FileUtils.writeStringToFile(file, document.get());

	    // to iterate through methods
	    List<AbstractTypeDeclaration> types = unit.types();
	    for (AbstractTypeDeclaration type : types) {
	        if (type.getNodeType() == ASTNode.TYPE_DECLARATION) {
	            // Class def found
	            List<BodyDeclaration> bodies = type.bodyDeclarations();
	            for (BodyDeclaration body : bodies) {
	                if (body.getNodeType() == ASTNode.METHOD_DECLARATION) {
	                    MethodDeclaration method = (MethodDeclaration)body;
	                    System.out.println("name: " + method.getName().getFullyQualifiedName());
	                }
	            }
	        }
	    }
	}
}
