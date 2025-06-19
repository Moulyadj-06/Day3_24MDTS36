package org.example.student;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Scanner;

public class Student {
    public static void main(String[] args) {
        MongoDatabase db = MongoDBConnection.getDatabase("StudentDB");
        MongoCollection<Document> students = db.getCollection("students");
        MongoCollection<Document> courses = db.getCollection("courses");
        MongoCollection<Document> enrollments = db.getCollection("enrollments");

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== MENU ===");
            System.out.println("1. Insert Student");
            System.out.println("2. Insert Course");
            System.out.println("3. Enroll (Embedded)");
            System.out.println("4. Enroll (Referenced)");
            System.out.println("5. Show All Enrollments");
            System.out.println("6. Update Student Name");
            System.out.println("7. Exit");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter student name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter age: ");
                    int age = sc.nextInt();
                    Document student = new Document("name", name).append("age", age);
                    students.insertOne(student);
                    System.out.println("Student inserted.");
                }
                case 2 -> {
                    System.out.print("Enter course title: ");
                    String title = sc.nextLine();
                    System.out.print("Enter credits: ");
                    int credits = sc.nextInt();
                    Document course = new Document("title", title).append("credits", credits);
                    courses.insertOne(course);
                    System.out.println("Course inserted.");
                }
                case 3 -> {
                    System.out.print("Enter student name to embed: ");
                    String sName = sc.nextLine();
                    System.out.print("Enter course title to embed: ");
                    String cTitle = sc.nextLine();
                    Document student = students.find(new Document("name", sName)).first();
                    Document course = courses.find(new Document("title", cTitle)).first();
                    if (student != null && course != null) {
                        Document enroll = new Document("type", "embedded")
                                .append("student", student)
                                .append("course", course);
                        enrollments.insertOne(enroll);
                        System.out.println("Enrollment saved (embedded).");
                    } else {
                        System.out.println("Student or Course not found.");
                    }
                }
                case 4 -> {
                    System.out.print("Enter student name to reference: ");
                    String sName = sc.nextLine();
                    System.out.print("Enter course title to reference: ");
                    String cTitle = sc.nextLine();
                    Document student = students.find(new Document("name", sName)).first();
                    Document course = courses.find(new Document("title", cTitle)).first();
                    if (student != null && course != null) {
                        Document enroll = new Document("type", "referenced")
                                .append("student_id", student.getObjectId("_id"))
                                .append("course_id", course.getObjectId("_id"));
                        enrollments.insertOne(enroll);
                        System.out.println("Enrollment saved (referenced).");
                    } else {
                        System.out.println("Student or Course not found.");
                    }
                }
                case 5 -> {
                    System.out.println("\n-- Embedded Enrollments --");
                    for (Document d : enrollments.find(new Document("type", "embedded"))) {
                        Document stu = (Document) d.get("student");
                        Document cou = (Document) d.get("course");
                        System.out.println(stu.getString("name") + " -> " + cou.getString("title"));
                    }

                    System.out.println("\n-- Referenced Enrollments --");
                    for (Document d : enrollments.find(new Document("type", "referenced"))) {
                        Document stu = students.find(new Document("_id", d.getObjectId("student_id"))).first();
                        Document cou = courses.find(new Document("_id", d.getObjectId("course_id"))).first();
                        if (stu != null && cou != null) {
                            System.out.println(stu.getString("name") + " -> " + cou.getString("title"));
                        }
                    }
                }
                case 6 -> {
                    System.out.println("Students in DB:");
                    for (Document doc : students.find()) {
                        System.out.println(doc.getObjectId("_id") + " : " + doc.getString("name"));
                    }

                    System.out.print("Enter student ID to update: ");
                    String idStr = sc.nextLine();
                    System.out.print("Enter new name: ");
                    String newName = sc.nextLine();

                    try {
                        ObjectId id = new ObjectId(idStr);
                        students.updateOne(new Document("_id", id),
                                new Document("$set", new Document("name", newName)));
                        System.out.println("Student name updated.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid ID format.");
                    }
                }
                case 7 -> {
                    System.out.println("Exiting...");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

}
