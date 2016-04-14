package org.ground.palico.hdf;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class HDFCreator {
    private String fileName;
    private String dataSetName;
    private float[] dataSet;

    private static final String STRUCTMETADATA = "data/structMetadata.txt";
    private static final int DIM_X = 5685;
    private static final int DIM_Y = 5567;
    private static final int SDIM = 3200;
    private static final long[] dims = {DIM_X, DIM_Y};
    private static final long[] dims2 = {1};

    private static final String GROUPNAME1 = "HDFEOS";

    private static final String GROUPNAME1_1 = "GRIDS";
    private static final String GROUPNAME1_1_1 = "Image Data";
    private static final String GROUPNAME1_1_1_1 = "Data Fields";
    private static final String GROUPNAME2 = "HDFEOS INFORMATION";

    public HDFCreator(String fileName, String dataSetName, float[] dataSet) {
        this.fileName = fileName;
        this.dataSetName = dataSetName;
        this.dataSet = dataSet;
    }

    public void create() throws Exception {

        File file = new File(STRUCTMETADATA);
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String temp;
        String content = "";
        while ((temp = br.readLine()) != null) {
            content += temp + "\n";
        }
        String[] structMetadata = {content};

        int file_id = H5.H5Fcreate(fileName, HDF5Constants.H5F_ACC_TRUNC, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        int groupId1 = H5.H5Gcreate(file_id, "/" + GROUPNAME1, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        int groupId1_1 = H5.H5Gcreate(file_id, "/" + GROUPNAME1 + "/" + GROUPNAME1_1, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        int groupId1_1_1 = H5.H5Gcreate(file_id, "/" + GROUPNAME1 + "/" + GROUPNAME1_1 + "/" + GROUPNAME1_1_1, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        int groupId1_1_1_1 = H5.H5Gcreate(file_id, "/" + GROUPNAME1 + "/" + GROUPNAME1_1 + "/" + GROUPNAME1_1_1 + "/" + GROUPNAME1_1_1_1, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);

        int groupId2 = H5.H5Gcreate(file_id, "/" + GROUPNAME2, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);

        int dataSpaceId1_1 = H5.H5Screate_simple(2, dims, null);
        int dataSetId1_1 = H5.H5Dcreate(groupId1_1_1_1, dataSetName, HDF5Constants.H5T_IEEE_F64LE, dataSpaceId1_1, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        H5.H5Dwrite(dataSetId1_1, HDF5Constants.H5T_NATIVE_FLOAT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, dataSet);

        int dataSpaceId1_2 = H5.H5Screate_simple(1, dims2, null);
        int fileTypeId = H5.H5Tcopy(HDF5Constants.H5T_FORTRAN_S1);
        H5.H5Tset_size(fileTypeId, SDIM);
        int memTypeId = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
        H5.H5Tset_size(memTypeId, SDIM);
        int dataSetId1_2 = H5.H5Dcreate(groupId2, "StructMetadata.0", fileTypeId, dataSpaceId1_2, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);

        byte[] dSetData = new byte[SDIM];
        for (int i = 0; i < SDIM; i++) {
            if (i < structMetadata[0].length())
                dSetData[i] = (byte) structMetadata[0].charAt(i);
            else {
                dSetData[i] = 0;
            }
        }

        H5.H5Dwrite(dataSetId1_2, memTypeId, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, dSetData);

        H5.H5Dclose(dataSetId1_2);
        H5.H5Dclose(dataSetId1_1);
        H5.H5Gclose(groupId2);

        H5.H5Gclose(groupId1_1_1_1);
        H5.H5Gclose(groupId1_1_1);
        H5.H5Gclose(groupId1_1);
        H5.H5Gclose(groupId1);
        H5.H5Fclose(file_id);
    }
}