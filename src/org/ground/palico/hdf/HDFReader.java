package org.ground.palico.hdf;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;

public class HDFReader {
    String filePath;
    int size;

    public HDFReader(String filePath) {
        this.filePath = filePath;
    }

    public int[] getDataSet(int bandNum) throws Exception {
        final int fid = H5.H5Fopen(filePath, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_FILE_ACCESS_DEFAULT);
        String name = String.format("/HDFEOS/GRIDS/Image Data/Data Fields/Band %d Image Pixel Values", bandNum);

        final int did = H5.H5Dopen(fid, name, HDF5Constants.H5P_DEFAULT);
        final int type = H5.H5Dget_type(did);
        size = (int) H5.H5Dget_storage_size(did);
        int[] dataSet = new int[size];

        H5.H5Dread(did, type, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DATASET_XFER_DEFAULT, dataSet);
        H5.H5Dclose(did);
        H5.H5Fclose(fid);

        return dataSet;
    }

    public int getSize() {
        return size;
    }
}