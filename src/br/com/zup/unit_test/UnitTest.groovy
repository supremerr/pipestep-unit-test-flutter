
package br.com.zup.unit_test

class UnitTest{
    def call (jenkins) {
        jenkins.podTemplate(
            containers: [
                jenkins.containerTemplate(
                    name: 'flutter', 
                    image: 'cirrusci/flutter:2.0.1', 
                    ttyEnabled: true, 
                    command: 'cat'
                )
            ],
            yamlMergeStrategy: jenkins.merge(),
            workspaceVolume: jenkins.persistentVolumeClaimWorkspaceVolume(
                claimName: "pvc-${jenkins.env.JENKINS_AGENT_NAME}",
                readOnly: false
            )
        )

        {
            jenkins.node(jenkins.POD_LABEL){
                jenkins.container('flutter'){
                    try {
                        jenkins.sh label: "Flutter Clean", 
                                   script: "flutter clean"
                                   
                        jenkins.sh label: "Unit Tests", 
                                   script: "flutter test --coverage test/*"
                    } catch (Exception e) {
                        jenkins.unstable("AN error occured during build step. Please, verify the logs.")
                    }
                    
                }
            }
        }
    }
}