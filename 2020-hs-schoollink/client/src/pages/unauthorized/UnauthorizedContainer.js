import React from 'react';
import { BrowserRouter, Link, Switch, Route } from 'react-router-dom';
import Home from './Home';
import Staff from './Staff';
import AuthService from '../../services/AuthService';
import '../../assets/tailwind.css';

export default class UnauthorizedContainer extends React.Component {
  constructor() {
    super();
    this.state = { hidden: true }
  }
  onNavButtonClick() {
    console.log('button clicked')
    this.setState({hidden:false})
  }
    render() {
        return <BrowserRouter>
        <div class='flex flex-col min-h-screen bg-gray-100'>
        <div class="pt-6 px-4 sm:px-6 lg:px-8">
          <nav class="relative flex items-center justify-between sm:h-10 lg:justify-start">
            <div class="flex items-center flex-grow flex-shrink-0 lg:flex-grow-0">
              <div class="flex items-center justify-between w-full md:w-auto">
                <Link to="/">
                  SchoolLink New Zealand
                    </Link>
                <div class="ml-12 -mr-2 flex items-center hidden md:block">
                  <span class="ml-2 font-medium text-gray-500 hover:text-gray-900 focus:outline-none focus:text-gray-900 transition duration-150 ease-in-out"><Link to="/">Home</Link></span>
                  <span class="ml-8 font-medium text-gray-500 hover:text-gray-900 focus:outline-none focus:text-gray-900 transition duration-150 ease-in-out"><Link to="/staff">People</Link></span>
                  <span href="#" class="ml-8 font-medium text-red-600 hover:text-red-900 focus:outline-none focus:text-red-700 transition duration-150 ease-in-out"><Link to="/login">Testers login</Link></span>
                </div>

                <button onClick={(() => {this.setState({hidden:false})})} type="button" class="md:hidden inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:bg-gray-100 focus:text-gray-500 transition duration-150 ease-in-out">
                    <svg class="h-6 w-6" stroke="currentColor" fill="none" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
                    </svg>
                  </button>
              </div>
            </div>
          </nav>
        </div>

        <div hidden={this.state.hidden} class="z-40 absolute top-0 inset-x-0 p-2 md:hidden">
          <div class="rounded-lg shadow-md">
            <div class="rounded-lg bg-white shadow-xs overflow-hidden">
              <div class="px-5 pt-4 flex items-center justify-between">
                <div>
                  <img class="h-8 w-auto" src="/img/logos/workflow-mark-on-white.svg" alt="" />
                </div>
                <div class="-mr-2">
                  <button onClick={(() => {this.setState({hidden:true})})} type="button" class="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:bg-gray-100 focus:text-gray-500 transition duration-150 ease-in-out">
                    <svg class="h-6 w-6" stroke="currentColor" fill="none" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>
              </div>
              <div class="px-2 pt-2 pb-3">
                <Link onClick={(() => {this.setState({hidden:true})})} to="/"><span class="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50 focus:outline-none focus:text-gray-900 focus:bg-gray-50 transition duration-150 ease-in-out">Home</span></Link>
                <Link onClick={(() => {this.setState({hidden:true})})} to="/staff"><span class="mt-1 block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50 focus:outline-none focus:text-gray-900 focus:bg-gray-50 transition duration-150 ease-in-out">People</span></Link>
              </div>
              <div>
              <Link onClick={(() => {this.setState({hidden:true})})} to="/login">
                <span class="block w-full px-5 py-3 text-center font-medium text-red-600 bg-gray-50 hover:bg-gray-100 hover:text-red-700 focus:outline-none focus:bg-gray-100 focus:text-red-700 transition duration-150 ease-in-out">
                  Testers login
                  </span>
              </Link>
              </div>
            </div>
          </div>
        </div>

        <div class='flex-grow'>
        <Switch>
              <Route exact path="/" component={Home} />
              <Route path="/staff" component={Staff} />
              <Route exact path="/login" render={(props) => {
                AuthService.login();
              }} />
              <Route exact path="/covid-update" render={(props) => {
                return <div className="max-w-screen-xl mx-auto px-4 sm:px-6 lg:px-8 py-12 text-center">
                  <h3 class="mt-2 text-3xl leading-8 font-extrabold tracking-tight text-gray-900 sm:text-4xl sm:leading-10">
                    COVID-19 Update
                  </h3>
                  <div>Last updated April 4th, 2020</div>
                  <div className="border-t my-4">
                    <p class="text-base leading-6 text-red-600 font-semibold tracking-wide">Executive - Ryan Luo</p>
                    <div>
                      COVID-19 is a problem that fronts everyone in the SchoolLink Group partnership, and appropriate precautions are being made to ensure the health and safety of all staff.
                      All staff have been instructed to work from home from March 22nd until further notice, and are equipped with technology and resources to conduct their work as per normal. 
                    </div>
                  </div>
                  <div className="border-t my-4">
                    <p class="text-base leading-6 text-red-600 font-semibold tracking-wide">Operations - Jackson Rakena</p>
                    <div>
                      All SchoolLink systems are operating as normal.<br />Performance degradations may occur as the systems are heavily accessed, but the situation is being monitored constantly and resource allocations will be increased as required.
                      Contact <a href="mailto:operations@schoollink.co.nz" className="text-red-600 hover:underline">operations@schoollink.co.nz</a> for operational emergencies.
                    </div>
                  </div>
                  <div className="border-t my-4">
                    <p class="text-base leading-6 text-red-600 font-semibold tracking-wide">Experience - Benjamin Ofsoski</p>
                    <div>
                      As per the operational update, SchoolLink front-end services, including the website, mobile app, and API integrations should continue to work as normal. Mobile app issues should be reported to <a href="mailto:experience@schoollink.co.nz" className="text-red-600 hover:underline">experience@schoollink.co.nz</a> for assistance.
                    </div>
                  </div>
                  <div className="border-t my-4">
                    <p class="text-base leading-6 text-red-600 font-semibold tracking-wide">People - Vinh Peckler</p>
                    <div>In-person interviews and application testing has been cancelled, effective March 21st, 2020, 12:00 PM New Zealand Daylight Time.</div>
                  </div>
                </div>
              }} />
              <Route path="/startsession" render={(props) => {
                AuthService.handleAuthentication(props.history, window.location);
              }} />
              
              <Route>
                404, page not found.
              </Route>
        </Switch>
        </div>
        <footer class="w-full text-center border-t border-grey p-4 pin-b">
          <div class="text-gray-700">Developed in Wellington, New Zealand.</div>
          <div class="text-gray-700">Assets, logos, artwork and code Copyright &copy; 2019-2020 SchoolLink New Zealand.</div>
          <div class="text-gray-700"><span className="text-red-600 hover:underline"><Link to="/covid-update">COVID-19 Update</Link></span></div>
        </footer>
        </div>
      </BrowserRouter>;
    }
}