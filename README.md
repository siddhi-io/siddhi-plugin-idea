
# Siddhi plugin for Intellij IDEA

## Building from the source

1) Clone the plugin using 
```
git clone --recursive https://github.com/wso2/siddhi-plugin-idea.git
```
2. Install latest version of [Gradle](https://gradle.org/) if you don't have it installed already.

3. Navigate into the cloned repository and run `gradle buildPlugin`. In the **build/distributions** directory, **Siddhi-Intellij-Plugin.zip** will be created.

## Installing the plugin to IDEA

### From Jetbrains plugin repository
1. Go to **Settings** (**Preferences** in **MacOS**)-> **Plugins**. 
2. Select **Browse Repositories** button at the bottom. 
3. Search for **Siddhi** using the search box. Siddhi plugin will show up. Then you can install the plugin using the 
Install button.
4. Restart IDEA.

**Note:** Refer the [Getting Started](https://github.com/wso2/siddhi-plugin-idea#getting-started) section to view 
details about setting up the Siddhi SDK.

### From a local build
1. Go to **File -> Settings** (**IntelliJ IDEA -> Preferences** in **macOS**) and select **Plugins**.
2. Click **Install plugin from disc** button and select the deployed **plugin zip** file. Please make sure to install the Zip file, not the extracted Jar files. This zip contains an additional library as well. Without this library, the plugin will not work properly.
3. Restart IDEA.

## Release versions schema

Below you can see the versions of the plugin which correspond to the versions of the 
IntelliJ Platfom.

| Plugin Version | Platform Version |
| --- | --- |
| 0.5+ | IntelliJ IDEA 2017.2.5+ |

## Getting started

Please refer the [Getting Started](getting-started) section.

### Running siddhi files in IDEA

1) Go to Create New Project ->select Siddhi and click Next.

2) Click configure and select Home Directory for Siddhi SDK. Siddhi sdk is located in 
siddhi-sdk/modules/siddhi-launcher/target.

3) Click Next and give a project name.

4) Then create a new file (ex: Test.siddhi) and run the file using Edit Configurations->Defaults->Siddhi and choose the location of the siddhi file and press OK.

If you want to use an extension with the siddhi it can be done by adding the necessary dependency jar files into 
siddhi-sdk/modules/siddhi-launcher/target/siddhi-sdk-1.0.0-SNAPSHOT/lib/ directory. 
(Most of the extensions' dependency jars are packed in default)

## How to Contribute
 
  * Please report issues at <a target="_blank" href="https://github.com/wso2/siddhi-plugin-idea/issues">GitHub Issue 
  Tracker</a>.
  
  * Send your contributions as pull requests to <a target="_blank" href="https://github.com/wso2/siddhi-plugin-idea/tree/master">master branch</a>. 
 
## Contact us 

 * Post your questions with the <a target="_blank" href="http://stackoverflow.com/search?q=siddhi">"Siddhi"</a> tag in <a target="_blank" href="http://stackoverflow.com/search?q=siddhi">Stackoverflow</a>. 
 
 * Siddhi developers can be contacted via the mailing lists:
 
    Developers List   : [dev@wso2.org](mailto:dev@wso2.org)
    
    Architecture List : [architecture@wso2.org](mailto:architecture@wso2.org)
 
## Support 

* We are committed to ensuring support for this extension in production. Our unique approach ensures that all support leverages our open development methodology and is provided by the very same engineers who build the technology. 

* For more details and to take advantage of this unique opportunity contact us via <a target="_blank" href="http://wso2.com/support?utm_source=gitanalytics&utm_campaign=gitanalytics_Jul17">http://wso2.com/support/</a>.


