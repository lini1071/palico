package org.ground.palico.hdf;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;

/**
 * Created by shlee on 16. 3. 29.
 */
public class HDFCreator {
    String fileName;
    String dataSetName;
    double[] dataSet;

    private static final int DIM_X = 5685;
    private static final int DIM_Y = 5567;

    public HDFCreator(String fileName, String dataSetName, double[] dataSet) {
        this.fileName = fileName;
        this.dataSetName = dataSetName;
        this.dataSet = dataSet;
    }

    public void create() throws HDF5Exception {
        int file_id;
        int dataSpaceId;
        int dataSetId = -1;
        long[] dims = {DIM_X, DIM_Y};

        // Create a new file using default properties.
        file_id = H5.H5Fcreate(fileName, HDF5Constants.H5F_ACC_TRUNC, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);

        // Create the data space for the dataSet.
        dataSpaceId = H5.H5Screate_simple(2, dims, null);

        // Create the dataSet.
        if ((file_id >= 0) && (dataSpaceId >= 0))
            dataSetId = H5.H5Dcreate(file_id, "/" + dataSetName, HDF5Constants.H5T_IEEE_F64LE, dataSpaceId,
                    HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);

        // Write the dataSet.
        if (dataSetId >= 0)
            H5.H5Dwrite(dataSetId, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
                    HDF5Constants.H5P_DEFAULT, dataSet);

        /*if (dataSetId >= 0)
            H5.H5Dread(dataSetId, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
                    HDF5Constants.H5P_DEFAULT, dataSet);
*/
        // Close the dataSet.
        if (dataSetId >= 0)
            H5.H5Dclose(dataSetId);

        // Close the file.
        if (file_id >= 0)
            H5.H5Fclose(file_id);
    }


}
