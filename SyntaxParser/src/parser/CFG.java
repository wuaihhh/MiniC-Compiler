package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

public class CFG {
	
	public static String emp = "��";
	
	public static String end = "$";
	
	public static TreeSet<String> keywords = new TreeSet<String>();//�����ּ�
	
	public static TreeSet<String> VN = new TreeSet<String>();//���ս����
	public static TreeSet<String> VT = new TreeSet<String>();//�ս����
	public static ArrayList<Derivation> F = new ArrayList<Derivation>();//����ʽ��
	
	public static HashMap<String,TreeSet<String> > firstMap = new HashMap<String,TreeSet<String> >();//first
	public static HashMap<String,TreeSet<String> > followMap = new HashMap<String,TreeSet<String> >();//follow
	
	static{
		//���ļ��ж�ȡ�ķ���������Ӧ�Ĳ���ʽ
		try {
			read("cfg.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//����C���Եı�����
		String[] keyword ={
				"auto","double","int","struct","break","else","long","switch",
				"case","enum","register","typedef","char","return","union","const",
				"extern","float","short","unsigned","continue","for","signed","void",
				"default","goto","sizeof","volatile","do","if","static","while"
		};
		for(String k:keyword){
			keywords.add(k);
		}
		//���ӷ��ս��
		//VN.add("E");VN.add("T");VN.add("E'");VN.add("T'");VN.add("F");
		VN.add("S'");VN.add("S");VN.add("C");
		//�����ս��
		//VT.add("*");VT.add("id");VT.add("(");VT.add(")");VT.add("+");
		VT.add("c");VT.add("d");
		//VT.addAll(keywords);
		
		addFirst();
		//addFollow();
	}
	
	/**
	 * ���ļ��ж�ȡ�ķ����Ҵ洢��CFG��ľ�̬�����У���ž���������index
	 * @param filename
	 * @throws FileNotFoundException
	 */
	private static void read(String filename) throws FileNotFoundException{
		File file = new File("Input/"+filename);
		Scanner scanner = new Scanner(file);
		while(scanner.hasNext()){
			String line = scanner.nextLine();
			String[] div = line.split("->");
			String[] right = div[1].split("\\|");//���ϲ���д�Ķ������ʽ�����ɶ��
			for(String r:right){
				Derivation derivation = new Derivation(div[0]+"->"+r);
				F.add(derivation);//�洢����̬��������
			}
		}
		scanner.close();
	}
	
	/**
	 * �������з��ŵ�first����
	 * �м���Ҫ���ɲ��Ƶ���ʹ��һ���ݹ鷽���������
	 */
	private static void addFirst(){
		//�����е��ս����first����Ϊ����
		Iterator<String> iterVT = VT.iterator();
		while(iterVT.hasNext()){
			String vt = iterVT.next();
			firstMap.put(vt,new TreeSet<String>());
			firstMap.get(vt).add(vt);
		}
		//�������з��ս����first����
		Iterator<String> iterVN = VN.iterator();
		while(iterVN.hasNext()){
			String vn = iterVN.next();
			firstMap.put(vn, new TreeSet<String>());//��Ϊ��������û�н����漰firstMap�����Բ��طֳ�����whileѭ�����ϳ�һ�˼���
			int dSize = F.size();
			for(int i = 0;i < dSize;i++){
				Derivation d = F.get(i);
				if(d.left.equals(vn)){//��ʵ���Ե���������һ��������ȡ���������ø���
					if(VT.contains(d.list.get(0))){//����ǲ���ʽ�Ҷ˵�һ���ķ�������һ���ս������ֱ������
						firstMap.get(vn).add(d.list.get(0));
					} else {//�������ʽ�Ҷ˵�һ���ķ������Ǹ����ս��������Ҫ���еݹ����
						firstMap.get(vn).addAll(findFirst(d.list.get(0)));
					}
				}
			}
		}
	}
	
	/**
	 * һ�����ڲ���first�ĵݹ麯��
	 * @param vn
	 * @return
	 */
	private static TreeSet<String> findFirst(String vn){
		TreeSet<String> set = new TreeSet<String>();
		for(Derivation d:F){
			if(d.left.equals(vn)){
				if(VT.contains(d.list.get(0))){//����Ǹ��ս������ֱ�Ӽ���
					set.add(d.list.get(0));
				} else {
					if(!vn.equals(d.list.get(0))){//ȥ��������E->E*E��������ݹ飬�Ӷ���Ч����ջ���
						set.addAll(findFirst(d.list.get(0)));//�ٴεݹ�
					}
				}
			}
		}
		return set;
	}
	
//	/**
//	 * ���ڼ�����ս����follow
//	 */
//	private static void addFollow(){
//		Iterator<String> iterVN = VN.iterator();
//		HashMap<String,ArrayList<String> > hashmap = new HashMap<String,ArrayList<String> >();
//		while(iterVN.hasNext()){
//			String vn = iterVN.next();
//			followMap.put(vn, new TreeSet<String>());
//			hashmap.put(vn, new ArrayList<String>());
//			for(Derivation d:F){
//				if(d.list.contains(vn)){//������һ��ѭ������Ϊ�����˲���ʽ�Ҷ˴��ڶ����ͬԪ��
//					ArrayList<Integer> index = new ArrayList<Integer>();
//					for(int i = 0;i < d.list.size();i++){
//						if(d.list.get(i).equals(vn)){
//							index.add(i);
//						}
//					}
//					for(int i:index){
//						if(i == (d.list.size()-1)){//�����һ������ʽ�Ҷ˵�ĩβ������Ҫ����$
//							followMap.get(vn).add(CFG.end);
//							hashmap.get(vn).add(d.left);
//						} else {//�������ĩβ����ֱ�Ӽ��Ϻ�һ��Ԫ�ص�first
//							TreeSet<String> add = new TreeSet<String>();
//							Iterator<String> iter = firstMap.get(d.list.get(i+1)).iterator();
//							while(iter.hasNext()){
//								String value = iter.next();
//								if(!value.equals(CFG.emp)){//�˵��մ�
//									add.add(value);
//								}
//							}
//							followMap.get(vn).addAll(add);
//						}
//					}
//				}
//			}
//			Iterator<String> iter = hashmap.keySet().iterator();
//			while(iter.hasNext()){
//				String key = iter.next();
//				ArrayList<String> value = hashmap.get(key);
//				if(value.size() != 0){
//					for(String v:value){
//						followMap.get(key).addAll(followMap.get(v));
//						followMap.get(v).addAll(followMap.get(key));
//					}
//				}
//			}
//		}
//	}

}