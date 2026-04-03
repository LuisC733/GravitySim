#version 330 core
in vec3 fragNormal;
in vec3 fragPos;

out vec4 fragColor;

uniform vec3 lightPos;
uniform vec3 viewPos;
uniform vec3 lightColor;
uniform vec3 bodyColor;

void main(){
    vec3 ambient = lightColor * 0.1f;

    vec3 normal = normalize(fragNormal);
    vec3 lightDir = normalize(lightPos - fragPos);
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = lightColor * diff;

    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 reflectDir = reflect(-lightDir, normal);   
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    vec3 specular = lightColor * spec * 0.5;

    vec3 finalColor = (ambient + diffuse + specular) * bodyColor;

    fragColor = vec4(finalColor, 1.0f);
}