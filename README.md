
# Siddhi-plugin-idea

### Building from the source

1) Clone the siddi-sdk using 
```
git clone https://github.com/RAVEENSR/siddhi-sdk.git
```

2) Build the siddhi-sdk using 
```
mvn clean install
```

Now the siddhi-sdk-1.0.0-SNAPSHOT.zip zip file will be created in the siddhi-sdk/modules/siddhi-launcher/target path. Unzip it. 

3) Clone the plugin using 
```
git clone --recursive https://github.com/RAVEENSR/Siddhi-Intellij-Plugin.git
```

4) Install latest version of Gradle if you don't have it installed already.

5) Navigate into the cloned repository and run gradle buildPlugin. In the build/distributions directory, Siddhi-Intellij-Plugin.zip will be created.
Installing the plugin to IDEA

### From a local build

1) Go to File -> Settings (IntelliJ IDEA -> Preferences in macOS) and select Plugins.

2) Click Install plugin from disc button and select the deployed plugin zip file. Please make sure to install the Zip file, not the extracted Jar files.

3) Restart IDEA

### Running siddhi files in IDEA

1) Go to Create New Project ->select Siddhi and click Next.

2) Click configure and select Home Directory for Siddhi SDK. Siddhi sdk is located in 
siddhi-sdk/modules/siddhi-launcher/target.

3) Click Next and give a project name.

4) Then create a new file (ex: Test.siddhi) and run the file using Edit Configurations->Defaults->Siddhi and choose the location of the siddhi file and press OK.

If you want to use an extension with the siddhi it can be done by adding the necessary dependency jar files into 
siddhi-sdk/modules/siddhi-launcher/target/siddhi-sdk-1.0.0-SNAPSHOT/lib/ directory. 
(Most of the extensions' dependency jars are packed in default)

