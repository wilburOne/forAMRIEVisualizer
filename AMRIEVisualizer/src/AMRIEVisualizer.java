import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;


public class AMRIEVisualizer {

	static HashMap<String, HashMap<String, String>> sentIndexConcept = new HashMap<String, HashMap<String, String>>();
	static HashMap<String, HashMap<String, String>> sentIndexConceptAbbr = new HashMap<String, HashMap<String, String>>();
	static HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> sentParentChild = new HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String arg1 = "data/visualize/EventExtractionResult/";
//		String arg2 = "data/visualize/AMRNodeEdge/";
//		String arg3 = "data/visualize/forVisualize/";
//		visualizedAllFiles(arg1, arg2, arg3);
		
		if(args.length!=3){
			System.out.println("The args length should be 3: ");
			System.out.println("args[0]: The directory of event extraction result files");
			System.out.println("args[1]: The directory of AMR node edge files (These files can be generated with LiberalEvent's translator functions.)");
			System.out.println("args[2]: The directory of new AMR annotation files for visualization (including event trigger type and argument role)");
		}else{
			visualizedAllFiles(args[0].toString(), args[1].toString(), args[2].toString());
		}
		
	}
	
	public static void splitFile(String filePath, String resultPath){
		try{
			int index = 0;
			File fileDir = new File(filePath);
			File[] fileList = fileDir.listFiles();
			for(File file: fileList){
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = "";
				while((line=reader.readLine())!=null){
					ArrayList<String> list = new ArrayList<String>();
					while(line!=null && line.length()>0){
						list.add(line);
						line = reader.readLine();
					}
					
					BufferedWriter out = new BufferedWriter(new FileWriter(resultPath+index+".txt"));
					for(int i=0; i<list.size(); i++){
						out.write(list.get(i)+"\n");
					}
					out.close();
					index++;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void visualizedAllFiles(String filePath, String parsedFilePath, String resultPath){
		File fileDir = new File(filePath);
		File[] fileList = fileDir.listFiles();
		for(File file: fileList){
			visualizeFile(file.getAbsolutePath(), parsedFilePath, resultPath+file.getName());
		}
	}
	
	public static void visualizeFile(String file, String parsedFilePath, String resultFile){
		try{
			HashMap<String, Integer> triggerUsed = new HashMap<String, Integer>();
			
			HashMap<String, ArrayList<String>> map = readSentPath(parsedFilePath);
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			BufferedWriter out = new BufferedWriter(new FileWriter(resultFile));
			String line = "";

			String sentLine = "";
			String triggerLine = "";
			while((line=reader.readLine())!=null){
				// read each event mention
				sentLine = line;
				triggerLine = reader.readLine();
				ArrayList<String> argLines = new ArrayList<String>();
				line = reader.readLine();
				while(line!=null && line.length()>0){
					argLines.add(line);
					line=reader.readLine();
				}
				
				String[] triggerArray = triggerLine.split("	");
				String trigger = triggerArray[0];
				String[] triggerIndexArray = trigger.split("##");
				String sentIndex = triggerIndexArray[0]+"##"+triggerIndexArray[1];
				String triggertype = triggerArray[1];
				
				HashMap<String, String> indexConcept = sentIndexConcept.get(sentIndex);
				HashMap<String, HashMap<String, ArrayList<String>>> parentChild = sentParentChild.get(sentIndex);
				
				String triggerIndex = triggerIndexArray[2];
				
				if(parentChild.containsKey(triggerIndex)){
					HashMap<String, ArrayList<String>> tmp = parentChild.get(triggerIndex);
					ArrayList<String> l = new ArrayList<String>();
					
					int length0 = tmp.size()+2;
					String triggerTypeIndex = triggerIndex+"."+length0;
					while(indexConcept.containsKey(triggerTypeIndex)){
						length0++;
						triggerTypeIndex = triggerIndex+"."+length0;
					}
					//String triggerTypeIndex = triggerIndex+"."+length0;
					l.add(triggerTypeIndex);
					indexConcept.put(triggerTypeIndex, triggertype);
					
					tmp.put(":Trigger", l);
					parentChild.put(triggerIndex, tmp);
				}else{
					HashMap<String, ArrayList<String>> tmp = new HashMap<String, ArrayList<String>>();
					ArrayList<String> l = new ArrayList<String>();
					
					int length0 = tmp.size()+2;
					String triggerTypeIndex = triggerIndex+"."+length0;
					while(indexConcept.containsKey(triggerTypeIndex)){
						length0++;
						triggerTypeIndex = triggerIndex+"."+length0;
					}
					l.add(triggerTypeIndex);
					indexConcept.put(triggerTypeIndex, triggertype);
					
					tmp.put(":Trigger", l);
					parentChild.put(triggerIndex, tmp);
				}
				
				ArrayList<String> argIndexList = new ArrayList<String>();
				
				for(String argLine: argLines){
					String[] argLineArray = argLine.split("	");
					String argRole = argLineArray[argLineArray.length-1];
					argRole = argRole.replace("(", " ");
					argRole = argRole.replace(")", " ");
					argRole = argRole.replace("'", " ");
					argRole = argRole.replace(",", " ");
					while(argRole.contains("  ")){
						argRole = argRole.replace("  ", " ");
					}
					argRole = argRole.replaceAll(" ", "_");
					argRole = URLEncoder.encode(argRole);
					String[] argIndexArray = argLineArray[0].split("##");
					String argIndex = argIndexArray[2];
					
					argIndexList.add(argIndex);
					
					if(parentChild.containsKey(argIndex)){
						HashMap<String, ArrayList<String>> tmp = parentChild.get(argIndex);
						ArrayList<String> t = new ArrayList<String>();
						ArrayList<String> f = new ArrayList<String>();
						
						int length = tmp.size()+2;
						String trueResultIndex = argIndex+"."+length;
						while(indexConcept.containsKey(trueResultIndex)){
							length++;
							trueResultIndex = argIndex+"."+length;
						}
						indexConcept.put(trueResultIndex, argRole);
						t.add(trueResultIndex);						
						tmp.put(":ArgRole", t);
						parentChild.put(argIndex, tmp);
					}else{
						String trueResultIndex = argIndex+".1";
						indexConcept.put(trueResultIndex, argRole);
						ArrayList<String> t = new ArrayList<String>();
						t.add(trueResultIndex);
						HashMap<String, ArrayList<String>> tmp = new HashMap<String, ArrayList<String>>();
						tmp.put(":ArgRole", t);
						parentChild.put(argIndex, tmp);
					}
				}
				String g = graph("1", parentChild, indexConcept);
				
				ArrayList<String> sents = map.get(sentIndex);
				
				String key = "";
				if(triggerUsed.containsKey(trigger)){
					int c = triggerUsed.get(trigger)+1;
					key = trigger+"#"+c;
					triggerUsed.put(trigger, c);
				}else{
					int c = 1;
					triggerUsed.put(trigger, c);
					key = trigger+"#"+c;
				}
				out.write("# ::id " + key+ " ::date " + "\n");
				out.write("# ::snt " + sents.get(1) + "\n");
				out.write("# ::save-date " + sents.get(2) + "\n");
				out.write(g + "\n");
				out.write("\n");
				
				HashMap<String, ArrayList<String>> triggerChild = parentChild.get(triggerIndex);
				triggerChild.remove(":Trigger");
				parentChild.put(triggerIndex, triggerChild);
				for(String argIndex: argIndexList){
					HashMap<String, ArrayList<String>> argChild = parentChild.get(argIndex);
					argChild.remove(":ArgRole");
					parentChild.put(argIndex, argChild);
				}
			}
			out.close();
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static HashMap<String, ArrayList<String>> readSentFile(File file){
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String name = file.getName();
			name = name.replace(".txt", "");
			String line = "";
			String sentence = "";
			String tokSent = "";
			String align = "";
			sentence = reader.readLine();
			
			while((line=reader.readLine())!=null){
				
				String[] arraySent = sentence.split("	");
				sentence = arraySent[1];
				
				tokSent = line;
				String[]array = tokSent.split("	");
				String sentName = name+"##"+array[0];
				tokSent = array[1];
				
				line = reader.readLine();
				String[] arrayAlign = line.split("	");
				align = arrayAlign[1];
				
				line = reader.readLine();
				HashMap<String, String> indexConcept = new HashMap<String, String>();
				HashMap<String, String> indexConceptAbbr = new HashMap<String, String>();
				while(line.startsWith("::node")){
					String[] array1 = line.split("	");
					if(array1.length<4){
						//System.out.println(sentName + "	" + line);
					}else{
						String abbr = array1[1];
						String index = array1[2];
						String concept = array1[3];
						
						indexConcept.put(index, concept);
						indexConceptAbbr.put(index, abbr);
					}
					line = reader.readLine();
				}
				sentIndexConcept.put(sentName, indexConcept);
				sentIndexConceptAbbr.put(sentName, indexConceptAbbr);
				
				HashMap<String, HashMap<String, ArrayList<String>>> parentChild = new HashMap<String, HashMap<String, ArrayList<String>>>();
				while(line!=null && line.length()>0 && line.startsWith("::edge")){
					String[] array2 = line.split("	");
					String index1 = array2[1];
					String relation = array2[4];
					String index2 = array2[5];
					if(parentChild.containsKey(index1)){
						HashMap<String, ArrayList<String>> children = parentChild.get(index1);
						if(!children.containsKey(relation)){
							ArrayList<String> tmp = new ArrayList<String>();
							tmp.add(index2);
							children.put(relation, tmp);
						}else{
							ArrayList<String> tmp = children.get(relation);
							if(!tmp.contains(index2))tmp.add(index2);
							children.put(relation, tmp);
						}
						parentChild.put(index1, children);
					}else{
						ArrayList<String> tmp = new ArrayList<String>();
						tmp.add(index2);
						HashMap<String, ArrayList<String>> children = new HashMap<String, ArrayList<String>>();
						children.put(relation, tmp);
						parentChild.put(index1, children);
					}
					line=reader.readLine();
				}
				sentParentChild.put(sentName, parentChild);
				
				ArrayList<String> list = new ArrayList<String>();
				list.add(sentence);
				list.add(tokSent);
				list.add(align);
				map.put(sentName, list);
				
				sentence = line;
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	
	public static HashMap<String, ArrayList<String>> readSentPath(String files){
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		try{
			File fileDir = new File(files);
			File[] fileList = fileDir.listFiles();
			for(File file: fileList){
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String name = file.getName();
				name = name.replace(".txt", "");
				String line = "";
				String sentence = "";
				String tokSent = "";
				String align = "";
				sentence = reader.readLine();
				
				while((line=reader.readLine())!=null){
					
					String[] arraySent = sentence.split("	");
					sentence = arraySent[1];
					
					tokSent = line;
					String[]array = tokSent.split("	");
					String sentName = name+"##"+array[0];
					tokSent = array[1];
					
					line = reader.readLine();
					String[] arrayAlign = line.split("	");
					align = arrayAlign[1];
					
					line = reader.readLine();
					HashMap<String, String> indexConcept = new HashMap<String, String>();
					HashMap<String, String> indexConceptAbbr = new HashMap<String, String>();
					while(line.startsWith("::node")){
						String[] array1 = line.split("	");
						if(array1.length<4){
							//System.out.println(sentName + "	" + line);
						}else{
							String abbr = array1[1];
							String index = array1[2];
							String concept = array1[3].trim();
							
							//concept = URLEncoder.encode(concept);
							indexConcept.put(index, concept);
							indexConceptAbbr.put(index, abbr);
						}
						line = reader.readLine();
					}
					
					HashMap<String, HashMap<String, ArrayList<String>>> parentChild = new HashMap<String, HashMap<String, ArrayList<String>>>();
					while(line!=null && line.length()>0 && line.startsWith("::edge")){
						String[] array2 = line.split("	");
						String index1 = array2[1];
						String relation = array2[4];
						String index2 = array2[5];
						
						if(relation.equals(":wiki")){
							String wikiConcept = array2[7];
							wikiConcept = URLEncoder.encode(wikiConcept);
							indexConcept.put(index2, wikiConcept);
						}
						
						if(parentChild.containsKey(index1)){
							HashMap<String, ArrayList<String>> children = parentChild.get(index1);
							if(!children.containsKey(relation)){
								ArrayList<String> tmp = new ArrayList<String>();
								tmp.add(index2);
								children.put(relation, tmp);
							}else{
								ArrayList<String> tmp = children.get(relation);
								if(!tmp.contains(index2))tmp.add(index2);
								children.put(relation, tmp);
							}
							parentChild.put(index1, children);
						}else{
							ArrayList<String> tmp = new ArrayList<String>();
							tmp.add(index2);
							HashMap<String, ArrayList<String>> children = new HashMap<String, ArrayList<String>>();
							children.put(relation, tmp);
							parentChild.put(index1, children);
						}
						line=reader.readLine();
					}
					sentIndexConcept.put(sentName, indexConcept);
					sentIndexConceptAbbr.put(sentName, indexConceptAbbr);
					sentParentChild.put(sentName, parentChild);
					
					ArrayList<String> list = new ArrayList<String>();
					list.add(sentence);
					list.add(tokSent);
					list.add(align);
					map.put(sentName, list);
					
					sentence = line;
				}
				reader.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	
	public static String graph(String index, HashMap<String, HashMap<String, ArrayList<String>>> map, HashMap<String, String>concepts){
		String result = "";
		if(map.containsKey(index)){
			HashMap<String, ArrayList<String>> children = map.get(index);
			String newIndex = index.replace(".", "");
			result = "("+newIndex+" / "+ concepts.get(index);
			for(Entry<String, ArrayList<String>>entry: children.entrySet()){
				String relation = entry.getKey();
				ArrayList<String> tmpList = entry.getValue();
				for(String tmp: tmpList){
					result += " " + relation + " " + graph(tmp, map, concepts);
				}
			}
			result += ")";
		}else{
			String newIndex = index.replace(".", "");
			result = "("+newIndex+" / "+ concepts.get(index)+")";
		}
		return result;
	}
}
