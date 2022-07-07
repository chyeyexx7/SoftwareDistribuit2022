<template>
  <div id="app">
    <nav class="navbar navbar-light bg-dark sticky-top">
      <a class="navbar-brand">
        <span class="mb-0 h2" style="color: lightgray; padding-left: 20px">Sports Matches</span>
      </a>
      <div class="row">
        <h3 v-show="logged" class="profile-form">
          🙎‍♂️ {{ username }} &nbsp;&nbsp;
          <span style="font-size: 80%">
            &#128176; {{ money_available.toFixed(2) }} €</span>
        </h3>
        <button v-if="is_showing_cart"
                class="btn btn-outline-primary btn-md r-margin"
                @click="toggleCart"> Close cart </button>
        <button v-else
                class="btn btn-outline-primary btn-md r-margin"
                @click="toggleCart">
          View cart <span class="cart-count"> {{ matches_added.length }} </span>
        </button>
        <button v-if="logged" class="btn btn-outline-success btn-md r-margin" @click="logOut"> Sign Out </button>
        <button v-else class="btn btn-outline-primary btn-md r-margin" @click="goToLogin"> Sign In </button>
      </div>

    </nav>
    <div class="background">
      <!-- Cart start -->
      <div v-if="is_showing_cart" class="container" style="background-color: white; padding-top: 50px; padding-bottom: 50px">
        <h1>Cart</h1>
        <table v-if="matches_added.length > 0" class="table table-hover">
          <thead>
          <tr>
            <th style="text-align: center">Sport</th>
            <th style="text-align: center">Competition</th>
            <th style="text-align: center">Match</th>
            <th style="text-align: center">Quantity</th>
            <th style="text-align: center">Price(€)</th>
            <th style="text-align: center">Total</th>
            <th></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(x) in matches_added" :key="x.id">
            <td >{{ x['match'].competition.sport }}</td>
            <td style="text-align: center">{{ x['match'].competition.name }}</td>
            <td style="text-align: center">{{ x['match'].local.name }} vs {{ x['match'].visitor.name }}</td>
            <td style="text-align: center">{{ x['quantity'] }}
              <div class="btn-group" role="group">
                <button :disabled="x['quantity'] === 1"
                        class="btn btn-danger btn-md mr-1"
                        @click="returnTicket(x)">
                  <b>-</b>
                </button>
                <button class="btn btn-success btn-md" @click="buyTicket(x)"><b>+</b></button>
              </div>
            </td>
            <td style="text-align: center">{{ x['match'].price }}</td>
            <td style="text-align: center">{{ (x['match'].price * x['quantity']).toFixed(2) }}</td>
            <td style="text-align: center">
              <button class="btn btn-danger btn-md"
                      @click="deleteEventFromCart(x)">
                Delete Ticket
              </button>
            </td>
          </tr>
          </tbody>
        </table>
        <p v-else>Your cart is currently empty.</p>
        <div class="btn-group" role="group">
          <button class="btn btn-secondary btn-lg mr-1"
                  @click="toggleCart">
            <b>Back</b>
          </button>
          <button :disabled="matches_added.length === 0"
                  class="btn btn-success btn-lg"
                  @click="finalizePurchase">
            <b>Purchase</b>
          </button>
        </div>
      </div> <!-- Cart end -->
      <!-- Matches start -->
      <div v-else class="container">
        <div class="row">
          <div class="col-lg-4 col-md-6 mb-4" v-for="(match) in matches" :key="match.id">
            <div class="card" style="width:20rem;">
            <img class="card-img-top img-fluid" v-bind:src="require('../assets/' + match.competition.sport + '.jpeg')" >
            <div class="card-body">
              <h5 class="card-title">{{ match.competition.sport }} - {{ match.competition.category }}</h5>
              <h6> {{ match.competition.name }} </h6>
              <h6><strong>{{ match.local.name }}</strong> ({{ match.local.country }}) vs <strong>{{ match.visitor.name }}</strong> ({{ match.visitor.country }})</h6>
              <h6>{{ match.date.substring(0,10) }}</h6>
              <h6>{{ match.price }} &euro;</h6>
              <h6>Entrades disponibles: {{match.total_available_tickets}}</h6>
              <button :disabled="match.total_available_tickets === 0"
                      class="btn btn-success btn-lg"
                      @click=addEventToCart(match)>
                Add to cart
              </button>
            </div>
            </div>
          </div>
        </div>
      </div> <!-- Matches end -->
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data () {
    return {
      /* actual_path = 'http://localhost:5000/' */
      actual_path: 'https://f1-sportsmaster.herokuapp.com/',
      is_showing_cart: false,
      title: 'Sports matches',
      tickets_bought: 0,
      money_available: 0,
      logged: false,
      matches_added: [],
      matches: []
    }
  },
  methods: {
    goToLogin () {
      this.$router.replace({ path: '/login' })
    },
    logOut () {
      this.logged = false
      this.$router.replace({ path: '/' })
      location.reload()
    },
    toggleCart () {
      this.is_showing_cart = !this.is_showing_cart
    },
    buyTicket (tickets) {
      tickets['quantity']++
    },
    returnTicket (tickets) {
      if (tickets['quantity'] > 1) tickets['quantity']--
    },
    addEventToCart (match) {
      let event = {'match': match, 'quantity': 1}
      var filter = this.matches_added.filter((x) => {
        return x.match === match
      })
      if (!filter.length) this.matches_added.push(event)
      else alert('This match is already in cart.')
    },
    finalizePurchase () {
      if (!this.logged) {
        alert('You have to Sign In to be able to purchase tickets')
        this.goToLogin()
      } else {
        let tickets = []
        this.matches_added.forEach((element) => {
          const parameters = {
            match_id: element.match.id,
            tickets_bought: element.quantity
          }
          tickets.push(parameters)
        })
        this.addPurchase({orders: tickets})
      }
    },
    addPurchase (orders) {
      const path = this.actual_path + `/orders/${this.username}`
      axios.post(path, orders, {
        auth: {username: this.token}
      })
        .then(() => {
          console.log('Order done')
          this.matches_added = []
          this.getAccount()
          this.getMatches()
        })
        .catch((error) => {
          // eslint-disable-next-line
          alert(error.response.data.message)
        })
    },
    deleteEventFromCart (match) {
      let idx = this.matches_added.indexOf(match)
      this.matches_added.splice(idx, 1)
    },
    getMatches () {
      const pathMatches = this.actual_path + 'matches'
      const pathCompetition = this.actual_path + 'competition/'
      axios.get(pathMatches)
        .then((res) => {
          var matches = res.data['Matches List'].filter((match) => {
            return match.competition_id != null
          })
          var promises = []
          for (let i = 0; i < matches.length; i++) {
            const promise = axios.get(pathCompetition + matches[i].competition_id)
              .then((resCompetition) => {
                delete matches[i].competition_id
                matches[i].competition = {
                  'name': resCompetition.data.competition.name,
                  'category': resCompetition.data.competition.category,
                  'sport': resCompetition.data.competition.sport
                }
              })
              .catch((error) => {
                console.error(error)
              })
            promises.push(promise)
          }
          Promise.all(promises).then((_) => {
            this.matches = matches
          })
        })
        .catch((error) => {
          console.error(error)
        })
    },
    getAccount () {
      if (this.logged) {
        const path = this.actual_path + `account/${this.username}`
        axios.get(path, {
          auth: {username: this.token}
        })
          .then((res) => {
            if (res.data.account) {
              this.is_admin = res.data.account['is_admin']
              this.money_available = res.data.account['available_money']
            } else {
              this.logOut()
            }
          })
          .catch((error) => {
            console.error(error)
            this.logOut()
          })
      }
    }
  },
  created () {
    this.getMatches()
    this.username = this.$route.query.username
    if (this.$route.query.logged === undefined) {
      this.logged = false
    } else {
      this.logged = this.$route.query.logged
    }
    this.token = this.$route.query.token
    this.getAccount()
  }
}
</script>
<style scoped>

.container {
  min-width: 80%;
}

.background {
  padding: 50px 100px 50px 100px;
}

.r-margin {
  margin-right:10px;
}

.cart-count {
  background-color: #005cbf;
  color:lightgray;
  padding:0px 4px 0px 4px;
}

.profile-form {
  margin: 5px 15px 0px 0px;
  font-size: 140%;
  color:white;
}

.card-img-top {
  width: 100%;
  height: 25vh;
  object-fit: cover;
}

.row {
  margin-right:0;
  margin-left:0
}

</style>
