package com.satreci.shlee.hdf;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;

public class HDFCreator {
    String fileName;
    String dataSetName;
    float[] dataSet;

    private static final int DIM_X = 5685;
    private static final int DIM_Y = 5567;

    public HDFCreator(String fileName, String dataSetName, float[] dataSet) {
        this.fileName = fileName;
        this.dataSetName = dataSetName;
        this.dataSet = dataSet;
    }

    public void HDFCreate() throws HDF5Exception {
        int file_id = -1;
        int dataspace_id = -1;
        int dataset_id = -1;
        long[] dims = {DIM_X, DIM_Y};


        // Create a new file using default properties.
        file_id = H5.H5Fcreate(fileName, HDF5Constants.H5F_ACC_TRUNC,
                HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);

        // Create the data space for the dataset.
        dataspace_id = H5.H5Screate_simple(2, dims, null);

        // Create the dataset.
        if ((file_id >= 0) && (dataspace_id >= 0))
            dataset_id = H5.H5Dcreate(file_id, "/" + dataSetName,
                    HDF5Constants.H5T_STD_I32BE, dataspace_id,
                    HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);

        // Write the dataset.
        if (dataset_id >= 0)
            H5.H5Dwrite(dataset_id, HDF5Constants.H5T_NATIVE_INT,
                    HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
                    HDF5Constants.H5P_DEFAULT, dataSet);

        if (dataset_id >= 0)
            H5.H5Dread(dataset_id, HDF5Constants.H5T_NATIVE_INT,
                    HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
                    HDF5Constants.H5P_DEFAULT, dataSet);

        // Close the dataset.
        if (dataset_id >= 0)
            H5.H5Dclose(dataset_id);

        // Close the file.
        if (file_id >= 0)
            H5.H5Fclose(file_id);
    }


}
