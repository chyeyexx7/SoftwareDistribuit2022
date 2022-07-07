<template>
  <div id="app">
    <nav class="navbar navbar-light bg-dark sticky-top">
      <a class="navbar-brand">
        <span class="mb-0 h2" style="color: lightgray; padding-left: 20px">Sports Matches</span>
      </a>
    </nav>
    <!-- Creating account -->
    <div v-if="creatingAccount" class="card login-form">
      <h2 style="margin-bottom:40px">Create Account</h2>
      <div class="form-label-group">
        <label for="inputUsername" style="float:left">Username</label>
        <input type="username" id="input-Username" class="form-control"
               placeholder="Username" required autofocus v-model="addUserForm.username">
      </div>
      <div class="form-label-group">
        <br>
        <label for="inputPassword" style="float:left">Password</label>
        <input type="password" id="input-Password" class="form-control"
               placeholder="Password" required v-model="addUserForm.password">
      </div>
      <br>
      <div class="flex-parent jc-center buttonMargin">
        <button :disabled="!addUserForm.username || !addUserForm.password"
                class="btn btn-primary buttonWidth"
                @click="signUp()">
          <b>Sign Up</b>
        </button>
      </div>
      <div class="flex-parent jc-center buttonMargin">
        <button class="btn btn-secondary buttonWidth"
                @click="backToLogin()">Back to Sign In
        </button>
      </div>
    </div> <!-- Creating account -->
    <!-- Login -->
    <div v-else class="card login-form">
      <h2 style="margin-bottom:40px">Sign In</h2>
      <div class="form-label-group">
        <label for="inputUsername" style="float:left">Username</label>
        <input type="username" id="inputUsername" class="form-control"
               placeholder="Username" required autofocus v-model="username">
      </div>
      <div class="form-label-group">
        <br>
        <label for="inputPassword" style="float:left">Password</label>
        <input type="password" id="inputPassword" class="form-control"
               placeholder="Password" required v-model="password">
      </div>
      <br>
      <div class="flex-parent jc-center buttonMargin">
        <button :disabled="!username || !password"
                class="btn btn-primary buttonWidth"
                @click="checkLogin()">
          <b>Sign In</b>
        </button>
      </div>
      <div class="flex-parent jc-center buttonMargin">
        <button class="btn btn-success buttonWidth"
                @click="initCreateForm()">
          <b>Create Account</b>
        </button>
      </div>
      <div class="flex-parent jc-center buttonMargin">
        <button class="btn btn-secondary buttonWidth"
                @click="backToMatches()">Back to Matches
        </button>
      </div>
    </div><!-- Login -->
  </div>
</template>

<script>
import axios from 'axios'
export default {
  name: 'Login',
  data () {
    return {
      /* actual_path = 'http://localhost:5000/' */
      actual_path: 'https://f1-sportsmaster.herokuapp.com/',
      logged: false,
      username: null,
      password: null,
      token: null,
      creatingAccount: false,
      addUserForm: {
        username: null,
        password: null
      }
    }
  },
  methods: {
    /* LOGIN AND ACCOUNTS */
    checkLogin () {
      const parameters = {
        username: this.username,
        password: this.password
      }
      const path = this.actual_path + 'login'
      axios.post(path, parameters)
        .then((res) => {
          console.log('Login Successful')
          this.logged = true
          this.token = res.data.token
          alert('Sign In Successfully')
          this.$router.push({ path: '/', query: { username: this.username, logged: this.logged, token: this.token } })
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error)
          alert('Username or Password incorrect')
        })
    },
    initCreateForm () {
      this.creatingAccount = true
      this.addUserForm.username = null
      this.addUserForm.password = null
    },
    backToLogin () {
      this.creatingAccount = false
      this.username = null
      this.password = null
    },
    backToMatches () {
      this.$router.replace({ path: '/' })
    },
    signUp () {
      const parameters = {
        username: this.addUserForm.username,
        password: this.addUserForm.password
      }
      const path = this.actual_path + 'account'
      axios.post(path, parameters)
        .then(() => {
          console.log('Register Successful')
          alert('Sign Up Successfully')
          this.backToLogin()
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error)
          alert('Account already exists')
        })
    }
  }
}
</script>

<style scoped>
.buttonWidth {
  width: 100%;
}

.buttonMargin {
  margin: 5px 0px 5px 0px;
}

.login-form {
  margin:0 auto;
  margin-top: 100px;
  width: 35%;
  padding: 30px 40px 30px 40px;
  background-color: white;
  float: none;
}

</style>
