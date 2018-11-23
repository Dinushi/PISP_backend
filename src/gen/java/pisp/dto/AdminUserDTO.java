package pisp.dto;


import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class AdminUserDTO  {
  
  
  
  private String adminUsername = null;
  
  
  private String password = null;
  
  
  private String email = null;

  
  /**
   * changed user name of admin user
   **/
  @ApiModelProperty(value = "changed user name of admin user")
  @JsonProperty("adminUsername")
  public String getAdminUsername() {
    return adminUsername;
  }
  public void setAdminUsername(String adminUsername) {
    this.adminUsername = adminUsername;
  }

  
  /**
   * new password for admin user
   **/
  @ApiModelProperty(value = "new password for admin user")
  @JsonProperty("password")
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class AdminUserDTO {\n");
    
    sb.append("  adminUsername: ").append(adminUsername).append("\n");
    sb.append("  password: ").append(password).append("\n");
    sb.append("  email: ").append(email).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
