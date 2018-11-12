package org.launchcode.techjobs.console;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Created by LaunchCode
 */
public class JobData {

    private static final String DATA_FILE = "resources/job_data.csv";
    private static Boolean isDataLoaded = false;

    private static ArrayList<HashMap<String, String>> allJobs;

    /**
     * Fetch list of all values from loaded data,
     * without duplicates, for a given column.
     *
     * @param field The column to retrieve values from
     * @return List of all of the values of the given field
     */
    public static ArrayList<String> findAll(String field) {

        // load data, if not already loaded
        loadData();

        ArrayList<String> values = new ArrayList<>();

        for (HashMap<String, String> row : allJobs) {
            String aValue = row.get(field);

            if (!values.contains(aValue)) {
                values.add(aValue);
            }
        }

        // sort values so returns in alphabetical order
        Collections.sort(values);

        return values;
    }

    public static ArrayList<HashMap<String, String>> findAll() {

        // load data, if not already loaded
        loadData();

        return allJobs;
    }

    /**
     * Returns results of search the jobs data by key/value, using
     * inclusion of the search term.
     *
     * For example, searching for employer "Enterprise" will include results
     * with "Enterprise Holdings, Inc".
     *
     * @param column   Column that should be searched.
     * @param value Value of teh field to search for
     * @return List of all jobs matching the criteria
     */
    public static ArrayList<HashMap<String, String>> findByColumnAndValue(String column, String value) {

        // load data, if not already loaded
        loadData();

        ArrayList<HashMap<String, String>> jobs = new ArrayList<>();

        // for each row in allJobs
        for (HashMap<String, String> row : allJobs) {

            // hasAny is case insensitive. but checks all fields, not just one,
            // so shouldn't use. instead just:

            // add toLowerCase() so search is case insensitive:
            String aValue = row.get(column);

            if (aValue.toLowerCase().contains(value.toLowerCase())) {
                jobs.add(row);
            }


            // old code, not case insensitive:

            //String aValue = row.get(column);

            //if (aValue.contains(value)) {
            //    jobs.add(row);
            //}
        }

        return jobs;
    }

    public static ArrayList<HashMap<String, String>> findByValue(String value) {

        // load data, if not already loaded
        loadData();

        // make arraylist jobs, in which each hashmap is a single job:
        ArrayList<HashMap<String, String>> jobs = new ArrayList<>();

        for (HashMap<String, String> row : allJobs) {

            // use hasAny to check whether job (row) has the String value
            // anywhere in it. if so, then add it to the arraylist of jobs:

            if (hasAny(row, value)) {

                jobs.add(row);
            }
        }

        return jobs;
    }

    // make helper function hasAny to check if job contains search value:
    public static Boolean hasAny(HashMap<String, String> aJob, String searchValue) {

        Set<String> fields = aJob.keySet();
        // ^keySet() is a method of HashMap that returns a set (called fields)
        // of all the keys in the HashMap aJob

        for (String f : fields) {
            //get value of f:
            String fValue = aJob.get(f);

            // compare lowercase value of f to lowercase searchvalue
            // (so that search is case insensitive)
            if (fValue.toLowerCase().contains(searchValue.toLowerCase())) {
                return true;
            }

        }

        // if loops through all the fields and doesn't return true:
        return false;

    }


    /**
     * Read in data from a CSV file and store it in a list
     */
    private static void loadData() {

        // Only load data once
        if (isDataLoaded) {
            return;
        }

        try {

            // Open the CSV file and set up pull out column header info and records
            Reader in = new FileReader(DATA_FILE);
            CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            List<CSVRecord> records = parser.getRecords();
            Integer numberOfColumns = records.get(0).size();
            String[] headers = parser.getHeaderMap().keySet().toArray(new String[numberOfColumns]);

            allJobs = new ArrayList<>();

            // Put the records into a more friendly format
            for (CSVRecord record : records) {
                HashMap<String, String> newJob = new HashMap<>();

                for (String headerLabel : headers) {
                    newJob.put(headerLabel, record.get(headerLabel));
                }

                allJobs.add(newJob);
            }

            // flag the data as loaded, so we don't do it twice
            isDataLoaded = true;

        } catch (IOException e) {
            System.out.println("Failed to load job data");
            e.printStackTrace();
        }
    }

}
