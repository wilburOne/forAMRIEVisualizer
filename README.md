# forAMRIEVisualizer
This jar file is used to put liberal event extraction results into AMR annotation, and visualize this new annotation with Xiaoman's visualizer.

It takes three arguments:

    args[0]: The directory of event extraction result files, e.g., forAMRVisualizer/AMRIEVisualizer/data/visualize/
    
    args[1]: The directory of AMR node edge files (These files can be generated with LiberalEvent's translator functions.) e.g., forAMRVisualizer/AMRIEVisualizer/data/AMRNodeEdge/
	
    args[2]: The directory of new AMR annotation files for visualization (including event trigger type and argument role) e.g., forAMRVisualizer/AMRIEVisualizer/data/forVisualize/


Usage:

java -jar AMRIEVisualizer.jar < directory of event extraction result files> < directory of AMR node edge files> < directory of new AMR annotation files>

The new AMR annotations can be further used as input to the AMR Visualizer: https://github.com/panx27/amr-reader

Note: In the new AMR annotations, we will add one new relation :Trigger for trigger type visualization and another new relation :ArgRole for event argument role visualization.

Further, each concept will be created with a new short index (e.g., 1121) based on the position of the current node in the whole AMR graph. This short index is different from the index in original AMR parsing output.
