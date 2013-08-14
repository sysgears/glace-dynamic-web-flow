package com.sysgears.gdwf.repository

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.core.ConfigurableObjectInputStream
import org.springframework.util.FileCopyUtils

import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Flow serializer.
 */
class FlowSerializer {

    /**
     * Serializes given object.
     *
     * @param object object to serialize
     * @return serialized object
     * @throws IOException in case if an IO error occurred
     */
    static byte[] serialize(def object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ObjectOutputStream oos = new ObjectOutputStream(bos)
        try {
            oos.writeObject(object)
            oos.flush()
            return bos.toByteArray()
        } finally {
            oos.close()
        }
    }

    /**
     * Deserializes given object.
     *
     * @param object data to deserialize
     * @return deserialized object
     * @throws IOException in case if an IO error occurred
     * @throws ClassNotFoundException in case if Class of a serialized object cannot be found
     */
    static def deserialize(byte[] data) throws IOException, ClassNotFoundException {
        def bin = new ByteArrayInputStream(data)
        ObjectInputStream ois = new ConfigurableObjectInputStream(bin, ApplicationHolder.application.classLoader)
        try {
            return ois.readObject()
        } finally {
            ois.close()
        }
    }

    /**
     * Compresses given data using GZIP file format.
     *
     * @param dataToCompress data to compress
     * @return compressed data
     * @throws IOException in case if an IO error occurred
     */
    static byte[] compress(byte[] dataToCompress) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        GZIPOutputStream gzipos = new GZIPOutputStream(baos)
        try {
            gzipos.write(dataToCompress)
            gzipos.flush()
        } finally {
            gzipos.close()
        }

        baos.toByteArray()
    }

    /**
     * Decompresses given data from the GZIP file format.
     *
     * @param dataToCompress data to decompress
     * @return decompressed data
     * @throws IOException in case if an IO error occurred
     */
    static byte[] decompress(byte[] dataToDecompress) throws IOException {
        GZIPInputStream gzipin = new GZIPInputStream(new ByteArrayInputStream(dataToDecompress))
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        try {
            FileCopyUtils.copy(gzipin, baos)
        } finally {
            gzipin.close()
        }

        baos.toByteArray()
    }
}
