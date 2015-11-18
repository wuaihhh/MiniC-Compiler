package parser;

import java.util.ArrayList;
import java.util.Stack;

import lex.LexicalAnalyzer;
import lex.Token;
import lex.Type;

public class SyntaxParser {
	
	private LexicalAnalyzer lex;//�ʷ�������
	private ArrayList<Token> tokenList;//�Ӵʷ���������õ�����token,�൱��ģ���е�����
	private int length;//tokenlist�ĳ���
	private int index;//������ָ��tokenλ��
	
	private AnalyzeTable table;//������﷨������
	private Stack<Integer> stateStack;//���ڴ洢��Ӧ��״̬
	
	private Error error = null;
	
	public SyntaxParser(String filename){
		this.lex = new LexicalAnalyzer(filename);
		this.tokenList = lex.getTokenList();
		this.length = this.tokenList.size();
		this.index = 0;
		this.table = new AnalyzeTable();
		this.stateStack = new Stack<Integer>();
		this.stateStack.push(0);
	}
	
	public Token readToken(){
		if(index < length){
			return tokenList.get(index++);
		} else {
			return null;
		}
	}
	
	public void analyze(){
		while(true){
			Token token = readToken();
			int valueType = token.type;
			String value = getValue(valueType);
			int state = stateStack.lastElement();
			String action = table.ACTION(state, value);
			if(action.startsWith("s")){
				int newState = Integer.parseInt(action.substring(1));
				stateStack.push(newState);
				System.out.print("����"+"\t");
				System.out.print("״̬��:"+stateStack.toString()+"\t");
				System.out.print("����:");
				printInput();
				System.out.println();
			} else if(action.startsWith("r")){
				Derivation derivation = CFG.F.get(Integer.parseInt(action.substring(1)));
				int r = derivation.list.size();
				index--;
				for(int i = 0;i < r;i++){
					stateStack.pop();
				}
				int s = table.GOTO(stateStack.lastElement(), derivation.left);
				stateStack.push(s);
				System.out.print("��Լ"+"\t");
				System.out.print("״̬��:"+stateStack.toString()+"\t");
				System.out.print("����:");
				printInput();
				System.out.println();
			} else if(action.equals(AnalyzeTable.acc)){
				System.out.println("�﷨�������!");
				return;
			} else {
				error();
				return;
			}
			
			
		}
	}
	
	
	private String getValue(int valueType){
		switch(valueType){
		case Type.ADD:
			return "+";
		case Type.SUB:
			return "-";
		case Type.MUL:
			return "*";
		case Type.DIV:
			return "/";
		case Type.ID:
			return "<id>";
		case Type.NUM:
			return "<num>";
		case Type.IF:
			return "if";
		case Type.ELSE:
			return "else";
		case Type.SEMICOLON:
			return ";";
		default:
			return null;
		}
	}
	/**
	 * ����
	 */
	public void error(){
		if(error != null){
			System.out.println(error.toString());
		}
	}
	
	private void printInput(){
		String output = "";
		for(int i = index;i < tokenList.size();i++){
			output += tokenList.get(i).value;
			output += " ";
		}
		System.out.print(output);
	}
	
}