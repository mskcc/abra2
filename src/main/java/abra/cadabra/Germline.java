package abra.cadabra;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abra.CompareToReference2;
import abra.Feature;
import abra.Logger;
import abra.ThreadManager;

public class Germline {

	private CompareToReference2 c2r;
	
	private Map<String, List<String>> chromosomeCalls = new HashMap<String, List<String>>();

	public void call(String reference, String bamfile, int numThreads) throws IOException, InterruptedException {
		c2r = new CompareToReference2();
		c2r.init(reference);
		
		outputHeader();
		
		ThreadManager threadManager = new ThreadManager(numThreads);
		
		for (String chromosome : c2r.getChromosomes()) {
			Feature region = new Feature(chromosome, 1, c2r.getChromosomeLength(chromosome));
			GermlineRunnable thread = new GermlineRunnable(threadManager, this, bamfile, 
				c2r, region);
			
			threadManager.spawnThread(thread);
		}
		
		threadManager.waitForAllThreadsToComplete();
		
		// Output calls.
		for (String chromosome : c2r.getChromosomes()) {
			for (String call : chromosomeCalls.get(chromosome)) {
				System.out.println(call);
			}
		}
		
		Logger.info("Cadabra done.");
	}
	
	void addCalls(String chromosome, List<String> calls) {
		Logger.info("Choromosome: %s done.", chromosome);
		synchronized(calls) {
			chromosomeCalls.put(chromosome, calls);
		}
	}
	
	private void outputHeader() {
		System.out.println("##fileformat=VCFv4.1");
		System.out.println("#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	SAMPLE");
	}
		
	public static void main(String[] args) throws Exception {
//		String normal = "/home/lmose/dev/abra/cadabra/normal_test2.bam";
//		String tumor = "/home/lmose/dev/abra/cadabra/tumor_test2.bam";
		
//		String reference = "/home/lmose/reference/chr1/1.fa";
//		String normal = "/home/lmose/dev/abra/cadabra/normal1.bam";
//		String tumor = "/home/lmose/dev/abra/cadabra/tumor1.bam";

		
//		String normal = "/home/lmose/dev/abra/cadabra/normal.abra4.sort.bam";
//		String tumor = "/home/lmose/dev/abra/cadabra/tumor.abra4.sort.bam";

//		String reference = "/home/lmose/reference/chr1/chr1.fa";
//		String normal = "/home/lmose/dev/abra/cadabra/t2/ntest.bam";
//		String tumor = "/home/lmose/dev/abra/cadabra/t2/ttest.bam";

		
//		String reference = "/home/lmose/reference/chr1/chr1.fa";
//		String normal = "/home/lmose/dev/abra/cadabra/ins/ntest.bam";
//		String tumor = "/home/lmose/dev/abra/cadabra/ins/ttest.bam";
		
		if (args.length < 3) {
			System.out.println("Usage: java -cp abra.jar abra.cadabra.Germline <reference> <bam> <num_threads>");
			System.exit(-1);
		}
		
		
		
		String reference = args[0];
		String bamfile = args[1];
		int threads = Integer.parseInt(args[2]);
		
		new Germline().call(reference, bamfile, threads);
	}
}
