package control

import (
	"fmt"
	"github.com/gorilla/sessions"
	"golang.org/x/oauth2"
	"golang.org/x/oauth2/github"
	"net/http"
	"os"
)

var LevelFull = 5
var LevelMid = 3
var LevelUser = 1

var store = sessions.NewCookieStore(os.Getenv("SESSION_KEY"))

var githubOauthConfig *oauth2.Config

/*func Oauth(r *http.Request) (int, error) {

}*/

func init() {
	githubOauthConfig = &oauth2.Config {
		RedirectURL: "http://localhost:8080/callback",
		ClientID: os.Getenv("GITHUB_CLIENT_ID"),
		ClientSecret: os.Getenv("GITHUB_CLIENT_SECRET"),
		Scopes: []string{"user:email"},
		Endpoint: oauth2.Endpoint{
			AuthURL: "https://github.com/login/oauth/authorize",
			TokenURL: "https://github.com/login/oauth/access_token",
		},
	}
}

func Authenticate(w http.ResponseWriter, r *http.Request) {
	var err error = nil

	session, err := store.Get(r, "msiproject")

	if err != nil {
		fmt.Fprintf(w, "session error %s", err.Error())
		return
	}

	if r.URL.Query().Get("state") != session.Values["state"] {
		fmt.Fprintf(w, "State unmatch!")
		return
	}

	token, err := githubOauthConfig.Exchange(oauth2.NoContext, r.URL.Query().Get("code"))
	if err != nil {
		fmt.Fprintf(w, "token problem")
		return
	}

	if !token.Valid() {
		fmt.Fprintf(w, "invalid token")
		return
	}

	client := oauth2.NewClient(githubOauthConfig.Client(oauth2.NoContext, token))

	user, _, err := client.Users.Get(context.Background(), "")
	if err != nil {
		fmt.Fprintf(w, "error getting name")
		return
	}

	session.Values["githubUsername"]  = user.name
	session.Values["githubToken"] = token
	session.Save(r, w)
	http.Redirect(w, r, "/", 302)

}
