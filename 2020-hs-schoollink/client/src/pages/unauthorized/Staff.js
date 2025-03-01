import React from 'react';

export default class Staff extends React.Component {
    render() {
        return <div class="py-12">
        <div class="max-w-screen-xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="lg:text-center">
            <p class="text-base leading-6 text-red-600 font-semibold tracking-wide uppercase">People</p>
            <h3 class="mt-2 text-3xl leading-8 font-extrabold tracking-tight text-gray-900 sm:text-4xl sm:leading-10">
              Meet our staff
            </h3>
            <p class="mt-4 max-w-2xl text-xl leading-7 text-gray-500 lg:mx-auto">
              SchoolLink Group is a New Zealand partnership established in 2019, student-led and student-driven.
            </p>
          </div>
      
          <div class="mt-10">
            <ul class="md:grid md:grid-cols-2 md:col-gap-8 md:row-gap-10">
              <li>
                <div class="flex">
                    <img class="-ml-2 inline-block h-16 w-16 rounded-full text-white shadow-solid" src="https://i.imgur.com/UQxRdr9.jpg" alt="" />
                  <div class="ml-4">
                    <p class="text-lg leading-6 text-red-600 font-semibold tracking-wide">Executive Director (Leadership)</p>
                    <h5 class="text-lg leading-6 font-medium text-gray-900">Ryan Z. Luo</h5>
                    <span class="text-sm">Year 12 - IB Economics, IB Computer Science</span>
                    <p class="mt-2 text-base leading-6 text-gray-500">
                      Providing direction for SchoolLink Limited, Ryan chairs company meetings, brings staff together, and helps the company reach its goals.
                    </p>
                  </div>
                </div>
              </li>
              <li class="mt-10 md:mt-0">
                <div class="flex">
                <img class="-ml-2 inline-block h-16 w-16 rounded-full text-white shadow-solid" src="https://i.imgur.com/qJ6n0zA.png" alt="" />
                  <div class="ml-4">
                  <p class="text-lg leading-6 text-red-600 font-semibold tracking-wide">Systems - Operations (Engineering)</p>
                    <h5 class="text-lg leading-6 font-medium text-gray-900">Jackson C. Rakena</h5>
                    <span class="text-sm">Year 12 - IB History, IB Computer Science</span>
                    <p class="mt-2 text-base leading-6 text-gray-500">
                      An expert in systems design, Jackson constructs and maintains the crucial systems that power SchoolLink behind the scenes.
                    </p>
                  </div>
                </div>
              </li>
              <li class="mt-10 md:mt-0">
                <div class="flex">
                <img class="-ml-2 inline-block h-16 w-16 rounded-full text-white shadow-solid" src="https://i.imgur.com/fFEDUrv.jpg" alt="" />
                  <div class="ml-4">
                  <p class="text-lg leading-6 text-red-600 font-semibold tracking-wide">People - Relations (Production)</p>
                    <h5 class="text-lg leading-6 font-medium text-gray-900">Vinh R. Peckler</h5>
                    <span class="text-sm">Year 12 - IB Mathematics, IB Physics</span>
                    <p class="mt-2 text-base leading-6 text-gray-500">
                      Mastering communications, Vinh handles interpersonal relationships internally and externally, and captures critical market research to deliver an excellent service.
                    </p>
                  </div>
                </div>
              </li>
              <li class="mt-10 md:mt-0">
                <div class="flex">
                <img class="-ml-2 inline-block h-16 w-16 rounded-full text-white shadow-solid" src="https://i.imgur.com/ujQSzPc.jpg" alt="" />
                  <div class="ml-4">
                  <p class="text-lg leading-6 text-red-600 font-semibold tracking-wide">Systems - Experience (Design)</p>
                    <h5 class="text-lg leading-6 font-medium text-gray-900">Benjamin G. Ofsoski</h5>
                    <span class="text-sm">Year 12 - IB Business Management, IB Computer Science</span>
                    <p class="mt-2 text-base leading-6 text-gray-500">
                      Working in the field of user experience, Ben is in charge of interface design, and works with students and parents to deliver critical experiences.
                    </p>
                  </div>
                </div>
              </li>
            </ul>
          </div>

          <div class="container mx-auto">
            <div class="mt-8 lg:flex sm:justify-start lg:justify-center">
              <div class="rounded-md shadow">
                <button class="w-full flex items-center justify-center px-4 py-3 border border-transparent text-base leading-6 font-medium rounded-md text-white bg-red-600 hover:bg-red-500 focus:outline-none focus:shadow-outline transition duration-150 ease-in-out md:py-4 md:text-lg md:px-10">
                  2020 Partnership Agreement
                </button>
              </div>
              <div class="rounded-md shadow mx-4">
                <button class="w-full flex items-center justify-center px-8 py-3 border border-transparent text-base leading-6 font-medium rounded-md text-white bg-red-400 hover:bg-red-500 focus:outline-none focus:shadow-outline transition duration-150 ease-in-out md:py-4 md:text-lg md:px-10">
                  2019 Project Proposal
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    }
}