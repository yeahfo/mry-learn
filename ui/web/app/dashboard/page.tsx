import {lusitana} from "@/app/ui/fonts";
import {customers, invoices, revenue} from "@/app/lib/placeholder-data";
import React from "react";
import RevenueChart from "@/app/ui/dashboard/revenue-chart";

import LatestInvoices from "@/app/ui/dashboard/latest-invoices";
import {Card} from "@/app/ui/dashboard/cards";
const response = await fetch('https://api.github.com/gists/public');
const data = await response.json();
export default async function Page() {

    console.log(data)
    return (
        <main>
            <h1 className={`${lusitana.className} mb-4 text-xl md:text-2xl`}>
                Dashboard
            </h1>
            <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
                <Card title="Collected" value={`$ ${invoices
                    .filter(when => when.status === 'paid')
                    .map(then => then.amount)
                    .reduce((acc, curr) => acc + curr, 0)}`} type="collected"/>
                <Card title="Pending" value={`$ ${invoices
                    .filter(when => when.status === 'pending')
                    .map(then => then.amount)
                    .reduce((acc, curr) => acc + curr, 0)}`} type="pending"/>
                <Card title="Total Invoices" value={invoices.length} type="invoices"/>
                <Card title="Total Customers" value={customers.length} type="customers"/>
            </div>
            <div className="mt-6 grid grid-cols-1 gap-6 md:grid-cols-4 lg:grid-cols-8">
                <RevenueChart revenue={revenue}/>
                <LatestInvoices latestInvoices={customers.map(then => {
                    return Object.assign({
                        id: then.id,
                        name: then.name,
                        image_url: then.image_url,
                        email: then.email,
                        amount: 0
                    })
                })}/>
            </div>
        </main>
    );
}